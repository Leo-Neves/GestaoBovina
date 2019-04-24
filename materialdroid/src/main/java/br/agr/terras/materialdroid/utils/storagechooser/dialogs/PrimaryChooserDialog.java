package br.agr.terras.materialdroid.utils.storagechooser.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.agr.terras.materialdroid.R;
import br.agr.terras.materialdroid.utils.storagechooser.StorageChooser;
import br.agr.terras.materialdroid.utils.storagechooser.StorageChooserView;
import br.agr.terras.materialdroid.utils.storagechooser.adapters.StorageChooserListAdapter;
import br.agr.terras.materialdroid.utils.storagechooser.models.Config;
import br.agr.terras.materialdroid.utils.storagechooser.models.Storages;
import br.agr.terras.materialdroid.utils.storagechooser.utils.DiskUtil;
import br.agr.terras.materialdroid.utils.storagechooser.utils.FileUtil;
import br.agr.terras.materialdroid.utils.storagechooser.utils.MemoryUtil;



public class PrimaryChooserDialog {

    private Activity activity;
    private Dialog dialog;
    private View mLayout;
    private ViewGroup mContainer;

    private TextView mPathChosen;

    private static final String INTERNAL_STORAGE_TITLE = "Internal Storage";
    private static final String EXTERNAL_STORAGE_TITLE = "External Storage";

    private static final String EXTERNAL_STORAGE_PATH_KITKAT = "/storage/extSdCard";

    private List<Storages> storagesList;
    private List<String> customStoragesList;
    private String TAG = "StorageChooser";
    private MemoryUtil memoryUtil = new MemoryUtil();
    private FileUtil fileUtil = new FileUtil();

    private Config mConfig;

    // day night flag
    private int mChooserMode;

    private SecondaryChooserDialog secondaryChooserDialog;

    public PrimaryChooserDialog(Activity activity){
        this.activity = activity;
    }

    private View getLayout(LayoutInflater inflater, ViewGroup container) {
        mConfig = StorageChooser.sConfig;
        mLayout = inflater.inflate(R.layout.storage_list, container, false);
        initListView(activity, mLayout, mConfig.isShowMemoryBar());

        if(StorageChooserView.CHOOSER_HEADING !=null) {
            TextView dialogTitle = (TextView) mLayout.findViewById(R.id.dialog_title);
            dialogTitle.setText(StorageChooserView.CHOOSER_HEADING);
        }

        return mLayout;
    }

    /**
     * storage listView related code in this block
     */
    private void initListView(Context context, View view, boolean shouldShowMemoryBar) {
        ListView listView = (ListView) view.findViewById(R.id.storage_list_view);

        // we need to populate before to get the internal storage path in list
        populateList();

        // if dev needs to skip overview and the primary path is not mentioned the directory
        // chooser or file picker will default to internal storage
        if(mConfig.isSkipOverview()) {
            if(mConfig.getPrimaryPath() == null) {

                // internal storage is always the first element (I took care of it :wink:)
                String dirPath = evaluatePath(0);
                showSecondaryChooser(dirPath);
            } else {

                // path provided by dev is the goto path for chooser
                showSecondaryChooser(mConfig.getPrimaryPath());
            }

        } else {
            listView.setAdapter(new StorageChooserListAdapter(storagesList, context, shouldShowMemoryBar));
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // if allowCustomPath is called then directory chooser will be the default secondary dialog
                if(mConfig.isAllowCustomPath()) {
                    // if developer wants to apply threshold
                    if(mConfig.isApplyThreshold()) {
                        startThresholdTest(i);
                    } else {
                        String dirPath = evaluatePath(i);
                        showSecondaryChooser(dirPath);
                    }
                } else {
                    String dirPath = evaluatePath(i);
                    if(mConfig.isActionSave()) {
                        String preDef = mConfig.getPredefinedPath();

                        if(preDef != null) {
                            // if dev forgot or did not add '/' at start add it to avoid errors
                            if(!preDef.startsWith("/")) {
                                preDef = "/" + preDef;
                            }
                            dirPath = dirPath + preDef;
                            DiskUtil.saveChooserPathPreference(mConfig.getPreference(), dirPath);
                        } else {
                            Log.w(TAG, "Predefined path is null set it by .withPredefinedPath() to builder. Saving root directory");
                            DiskUtil.saveChooserPathPreference(mConfig.getPreference(), dirPath);
                        }
                    } else {
                        //Log.d("StorageChooser", "Chosen path: " + dirPath);
                        if(mConfig.isApplyThreshold()) {
                            startThresholdTest(i);
                        } else {
                            if (StorageChooser.onSelectListener != null) {
                                StorageChooser.onSelectListener.onSelect(dirPath);
                            }
                        }
                    }
                }
                if (secondaryChooserDialog !=null)
                    secondaryChooserDialog.dismiss();
                PrimaryChooserDialog.this.dismiss();
            }
        });

    }

    /**
     * initiate to take threshold test
     * @param position list click index
     */

    private void startThresholdTest(int position) {
        String thresholdSuffix= mConfig.getThresholdSuffix();

        // if threshold suffix is null then memory threshold is also null
        if(thresholdSuffix != null) {
            long availableMem = memoryUtil.getAvailableMemorySize(evaluatePath(position));


            if (doesPassThresholdTest((long) mConfig.getMemoryThreshold(), thresholdSuffix, availableMem)) {
                String dirPath = evaluatePath(position);
                showSecondaryChooser(dirPath);
            } else {
                String suffixedAvailableMem = String.valueOf(memoryUtil.suffixedSize(availableMem, thresholdSuffix)) + " " + thresholdSuffix;
                Toast.makeText(activity, activity.getString(R.string.toast_threshold_breached, suffixedAvailableMem), Toast.LENGTH_SHORT).show();
            }
        } else {
            // THROW: error in log
            Log.e(TAG, "add .withThreshold(int size, String suffix) to your StorageChooser.Builder instance");
        }
    }

    /**
     * secondary choosers are dialogs apart from overview (CustomChooserFragment and FilePickerFragment)
     * Configs :-
     *     setType()
     *     allowCustomPath()
     *
     * @param dirPath root path(starting-point) for the secondary choosers
     */

    private void showSecondaryChooser(String dirPath) {

        switch (mConfig.getSecondaryAction()) {
            case StorageChooser.NONE:
                break;
            case StorageChooser.DIRECTORY_CHOOSER:
                secondaryChooserDialog = new SecondaryChooserDialog(mConfig.getActivity(), dirPath, false);
                secondaryChooserDialog.show();
                break;
            case StorageChooser.FILE_PICKER:
                secondaryChooserDialog = new SecondaryChooserDialog(mConfig.getActivity(), dirPath, true);
                secondaryChooserDialog.show();
                break;
        }
    }


    /**
     * evaluates path with respect to the list click position
     * @param i position in list
     * @return String with the required path for developers
     */
    private String evaluatePath(int i) {
        if(i == 0) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return "/storage/" + storagesList.get(i).getStorageTitle();
        }
    }

    /**
     * checks if available space in user's device is greater than the developer defined threshold
     *
     * @param threshold defined by the developer using Config.withThreshold()
     * @param memorySuffix also defined in Config.withThreshold() - check in GB, MB, KB ?
     * @param availableSpace statfs available mem in bytes (long)
     * @return if available memory is more than threshold
     */
    private boolean doesPassThresholdTest(long threshold, String memorySuffix, long availableSpace) {
        return memoryUtil.suffixedSize(availableSpace, memorySuffix) > threshold;
    }

    /**
     * populate storageList with necessary storages with filter applied
     */
    private void populateList() {
        storagesList = new ArrayList<>();

        File storageDir = new File("/storage");
        String internalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File[] volumeList = storageDir.listFiles();

        Storages storages = new Storages();

        // just add the internal storage and avoid adding emulated henceforth
        if(StorageChooserView.INTERNAL_STORAGE_TEXT !=null) {
            storages.setStorageTitle(StorageChooserView.INTERNAL_STORAGE_TEXT);
        } else {
            storages.setStorageTitle(INTERNAL_STORAGE_TITLE);
        }
        storages.setStoragePath(internalStoragePath);
        storages.setMemoryTotalSize(memoryUtil.formatSize(memoryUtil.getTotalMemorySize(internalStoragePath)));
        storages.setMemoryAvailableSize(memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(internalStoragePath)));
        storagesList.add(storages);


        for(File f: volumeList) {
            if(!f.getName().equals(MemoryUtil.SELF_DIR_NAME)
                    && !f.getName().equals(MemoryUtil.EMULATED_DIR_KNOX)
                    && !f.getName().equals(MemoryUtil.EMULATED_DIR_NAME)
                    && !f.getName().equals(MemoryUtil.SDCARD0_DIR_NAME)) {
                Storages sharedStorage = new Storages();
                String fPath = f.getAbsolutePath();
                sharedStorage.setStorageTitle(f.getName());
                sharedStorage.setMemoryTotalSize(memoryUtil.formatSize(memoryUtil.getTotalMemorySize(fPath)));
                sharedStorage.setMemoryAvailableSize(memoryUtil.formatSize(memoryUtil.getAvailableMemorySize(fPath)));
                sharedStorage.setStoragePath(fPath);
                storagesList.add(sharedStorage);
            }
        }

    }

    private Dialog onCreateDialog() {
        Dialog d = StorageChooser.dialog;
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(getLayout(LayoutInflater.from(activity), mContainer));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        d.getWindow().setAttributes(lp);
        return d;
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void show() {
        dialog = onCreateDialog();
        //if (secondaryChooserDialog==null)
            //dialog.show();
    }
}
