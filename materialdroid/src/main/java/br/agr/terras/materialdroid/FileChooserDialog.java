package br.agr.terras.materialdroid;

import android.app.Activity;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;

import br.agr.terras.materialdroid.utils.storagechooser.StorageChooser;


/**
 * Created by edson on 13/10/16.
 */
public class FileChooserDialog {

    private StorageChooser chooser;

    // filter on file extension
    private String extension = null;
    public FileChooserDialog setExtension(String extension) {
        this.extension = (extension == null) ? null : extension.toLowerCase();
        return this;
    }

    // file selection event handling
    public interface FileSelectedListener {
        void fileSelected(File file);
    }
    public FileChooserDialog setFileListener(FileSelectedListener fileListener) {
        this.fileListener = fileListener;
        return this;
    }
    private FileSelectedListener fileListener;

    public FileChooserDialog(StorageChooser.Builder builder){
        init(builder);
    }

    public FileChooserDialog (Activity activity) {
        StorageChooser.Builder builder = new StorageChooser.Builder();
        builder.withMemoryBar(false);
        builder.allowAddFolder(false);
        builder.allowCustomPath(true);
        builder.withPredefinedPath(Environment.getExternalStorageDirectory().getPath());
        builder.setType(StorageChooser.FILE_PICKER);
        builder.skipOverview(true);
        builder.withActivity(activity);
        init(builder);
    }

    private void init(@NonNull StorageChooser.Builder builder){
        chooser = builder.build();
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                if (fileListener!=null)
                    fileListener.fileSelected(new File(path));
            }
        });
    }

    public void showDialog() {
        chooser.show();
    }


}
