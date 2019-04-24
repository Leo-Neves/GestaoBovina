package br.agr.terras.materialdroid;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.agr.terras.materialdroid.utils.RandomString;
import br.agr.terras.materialdroid.utils.ValidaCNPJ;
import br.agr.terras.materialdroid.utils.ValidaCPF;
import br.agr.terras.materialdroid.utils.creditcard.CreditCard;
import br.agr.terras.materialdroid.utils.creditcard.CreditCardInterface;
import br.agr.terras.materialdroid.utils.creditcard.CreditCardPatterns;

/**
 * Created by leo on 23/11/15.
 */
@SuppressWarnings("ResourceAsColor")
public class EditText extends AppCompatEditText {
    private Activity activity;
    public static final int FLOATING_LABEL_NONE = 0;
    public static final int FLOATING_LABEL_NORMAL = 1;
    public static final int FLOATING_LABEL_HIGHLIGHT = 2;
    public static final int MODO_INTEIRO = 0;
    public static final int MODO_DECIMAL = 1;
    public static final int MODO_DATA = 2;
    public static final int MODO_TELEFONE = 3;
    public static final int MODO_CPF = 4;
    public static final int MODO_TEXTO = 5;
    public static final int MODO_REAL = 6;
    public static final int MODO_EMAIL = 7;
    public static final int MODO_CNPJ = 8;
    public static final int MODO_CARTAO = 9;
    public static final int MODO_CEP = 10;
    public static final int MODO_MES_ANO = 11;
    public static final int CAPS_TODOS = 0;
    public static final int CAPS_NENHUM = 1;
    public static final int CAPS_SENTENCAS = 2;
    public static final int CAPS_PALAVRAS = 3;
    public static final int CAPS_PADRAO = 4;
    private static final String cpfMask = "###.###.###-##";
    private static final String cnpjMask = "##.###.###/####-##";
    private String telefoneMask;
    private static final String telefoneMask8Digitos = "(##)####-####";
    private static final String telefoneMask9Digitos = "(##)#####-####";
    private static String dataMask = "##/##/####";
    private static final String dataMaskMesAno = "##/##";
    private static final String cartaoMask = "####.####.####.####";
    private static final String cepMask = "#####-###";
    private int modo;
    private int caps;
    private int extraPaddingTop;
    private int extraPaddingBottom;
    private int extraPaddingLeft;
    private int extraPaddingRight;
    private int floatingLabelTextSize;
    private int floatingLabelTextColor;
    private int bottomTextSize;
    private int floatingLabelPadding;
    private int bottomSpacing;
    private boolean floatingLabelEnabled;
    private boolean highlightFloatingLabel;
    private int backgroundColor;
    private int baseColor;
    private int innerPaddingTop;
    private int innerPaddingBottom;
    private int innerPaddingLeft;
    private int innerPaddingRight;
    private int primaryColor;
    private int errorColor;
    private int minCharacters;
    private int maxCharacters;
    private boolean singleLineEllipsis;
    private boolean floatingLabelAlwaysShown;
    private boolean helperTextAlwaysShown;
    private int bottomEllipsisSize;
    private int minBottomLines;
    private int minBottomTextLines;
    private float currentBottomLines;
    private float bottomLines;
    private String helperText;
    private int helperTextColor = -1;
    private String tempErrorText;
    private float floatingLabelFraction;
    private boolean floatingLabelShown;
    private float focusFraction;
    private Typeface accentTypeface;
    private Typeface typeface;
    private CharSequence floatingLabelText;
    private boolean hideUnderline;
    private int underlineColor;
    private boolean autoValidate;
    private boolean charactersCountValid;
    private boolean floatingLabelAnimating;
    private boolean checkCharactersCountAtBeginning;
    private Bitmap[] iconLeftBitmaps;
    private Bitmap[] iconRightBitmaps;
    private Bitmap[] clearButtonBitmaps;
    private boolean validateOnFocusLost;
    private boolean showClearButton;
    private boolean firstShown;
    private int iconSize;
    private int iconOuterWidth;
    private int iconOuterHeight;
    private int iconPadding;
    private boolean clearButtonTouched;
    private boolean clearButtonClicking;
    private ColorStateList textColorStateList;
    private ColorStateList textColorHintStateList;
    private ArgbEvaluator focusEvaluator = new ArgbEvaluator();
    private boolean cpfValido = false;
    private boolean cnpjValido = false;
    private boolean cartaoValido = false;
    private boolean cepValido = false;
    private SimpleDateFormat dataFormat;
    Paint paint = new Paint(1);
    TextPaint textPaint = new TextPaint(1);
    StaticLayout textLayout;
    ObjectAnimator labelAnimator;
    ObjectAnimator labelFocusAnimator;
    ObjectAnimator bottomLinesAnimator;
    OnFocusChangeListener innerFocusChangeListener;
    OnFocusChangeListener outerFocusChangeListener;
    private TextWatcher textWatcher;
    private OnTextChangeListener onTextChangeListener;
    private OnSalvarListener onSalvarListener;
    private OnErrorListener onErrorListener;
    private List<METValidator> validators;
    private METLengthChecker lengthChecker;
    private List<CreditCard> cartoesDisponiveis;
    private Drawable cartaoNaoEncontradoDrawable;
    private Drawable semCartaoDrawable;
    private CreditCard cartaoSelecionado;
    private CreditCardInterface interfaceCartao;
    private OnDateChangeListener onDateChangeListener;
    private final String TAG_DIALOG_DATE = "Date_" + RandomString.randomString(6);

    public EditText(Context context) {
        super(context);
        this.init(context, (AttributeSet) null);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    @TargetApi(21)
    public EditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (context instanceof Activity)
            activity = (Activity) context;
        this.iconSize = this.getPixel(32);
        this.iconOuterWidth = this.getPixel(48);
        this.iconOuterHeight = this.getPixel(32);
        this.bottomSpacing = 8;
        this.bottomEllipsisSize = 4;
        int defaultBaseColor = -16777216;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditText);
        this.textColorStateList = typedArray.getColorStateList(R.styleable.EditText_et_textColor);
        this.textColorHintStateList = typedArray.getColorStateList(R.styleable.EditText_et_textColorHint);
        this.baseColor = typedArray.getColor(R.styleable.EditText_et_baseColor, defaultBaseColor);
        this.backgroundColor = typedArray.getColor(R.styleable.EditText_et_backgroundColor, Color.TRANSPARENT);
        this.modo = typedArray.getInt(R.styleable.EditText_et_modo, MODO_TEXTO);
        this.caps = typedArray.getInt(R.styleable.EditText_et_caps, CAPS_PADRAO);
        TypedValue primaryColorTypedValue = new TypedValue();

        int defaultPrimaryColor;
        try {
            if (Build.VERSION.SDK_INT < 21) {
                throw new RuntimeException("SDK_INT less than LOLLIPOP");
            }

            context.getTheme().resolveAttribute(16843827, primaryColorTypedValue, true);
            defaultPrimaryColor = primaryColorTypedValue.data;
        } catch (Exception var14) {
            try {
                int fontPathForView = this.getResources().getIdentifier("colorPrimary", "attr", this.getContext().getPackageName());
                if (fontPathForView == 0) {
                    throw new RuntimeException("colorPrimary not found");
                }

                context.getTheme().resolveAttribute(fontPathForView, primaryColorTypedValue, true);
                defaultPrimaryColor = primaryColorTypedValue.data;
            } catch (Exception var13) {
                defaultPrimaryColor = this.baseColor;
            }
        }

        this.primaryColor = typedArray.getColor(R.styleable.EditText_et_primaryColor, defaultPrimaryColor);
        this.setFloatingLabelInternal(typedArray.getInt(R.styleable.EditText_et_floatingLabel, 0));
        this.errorColor = typedArray.getColor(R.styleable.EditText_et_errorColor, Color.parseColor("#e7492E"));
        this.minCharacters = typedArray.getInt(R.styleable.EditText_et_minCharacters, 0);
        this.maxCharacters = typedArray.getInt(R.styleable.EditText_et_maxCharacters, 0);
        this.singleLineEllipsis = typedArray.getBoolean(R.styleable.EditText_et_singleLineEllipsis, false);
        this.helperText = typedArray.getString(R.styleable.EditText_et_helperText);
        this.helperTextColor = typedArray.getColor(R.styleable.EditText_et_helperTextColor, -1);
        this.minBottomTextLines = typedArray.getInt(R.styleable.EditText_et_minBottomTextLines, 0);
        String fontPathForAccent = typedArray.getString(R.styleable.EditText_et_accentTypeface);
        if (fontPathForAccent != null && !this.isInEditMode()) {
            this.accentTypeface = this.getCustomTypeface(fontPathForAccent);
            this.textPaint.setTypeface(this.accentTypeface);
        }

        String fontPathForView1 = typedArray.getString(R.styleable.EditText_et_typeface);
        if (fontPathForView1 != null && !this.isInEditMode()) {
            this.typeface = this.getCustomTypeface(fontPathForView1);
            this.setTypeface(this.typeface);
        }

        this.floatingLabelText = typedArray.getString(R.styleable.EditText_et_floatingLabelText);
        if (this.floatingLabelText == null) {
            this.floatingLabelText = this.getHint();
        }

        this.floatingLabelPadding = this.bottomSpacing;
        this.floatingLabelTextSize = 12;
        this.floatingLabelFraction = typedArray.getFloat(R.styleable.EditText_et_floatingLabelFraction, 1);
        this.floatingLabelTextColor = typedArray.getColor(R.styleable.EditText_et_floatingLabelTextColor, -1);
        this.floatingLabelAnimating = typedArray.getBoolean(R.styleable.EditText_et_floatingLabelAnimating, true);
        this.bottomTextSize = 12;
        this.hideUnderline = typedArray.getBoolean(R.styleable.EditText_et_hideUnderline, false);
        this.underlineColor = typedArray.getColor(R.styleable.EditText_et_underlineColor, -1);
        this.autoValidate = typedArray.getBoolean(R.styleable.EditText_et_autoValidate, false);
        this.iconLeftBitmaps = this.generateIconBitmaps(typedArray.getResourceId(R.styleable.EditText_et_iconLeft, -1));
        this.iconRightBitmaps = this.generateIconBitmaps(typedArray.getResourceId(R.styleable.EditText_et_iconRight, -1));
        this.showClearButton = typedArray.getBoolean(R.styleable.EditText_et_clearButton, false);
        this.clearButtonBitmaps = this.generateIconBitmaps(R.drawable.et_ic_clear);
        this.iconPadding = this.getPixel(16);
        this.floatingLabelAlwaysShown = typedArray.getBoolean(R.styleable.EditText_et_floatingLabelAlwaysShown, false);
        this.helperTextAlwaysShown = typedArray.getBoolean(R.styleable.EditText_et_helperTextAlwaysShown, false);
        this.validateOnFocusLost = typedArray.getBoolean(R.styleable.EditText_et_validateOnFocusLost, false);
        this.checkCharactersCountAtBeginning = typedArray.getBoolean(R.styleable.EditText_et_checkCharactersCountAtBeginning, true);
        typedArray.recycle();
        int[] paddings = new int[]{16842965, 16842966, 16842967, 16842968, 16842969};
        TypedArray paddingsTypedArray = context.obtainStyledAttributes(attrs, paddings);
        int padding = 0;
        this.innerPaddingLeft = padding;
        this.innerPaddingTop = padding;
        this.innerPaddingRight = padding;
        this.innerPaddingBottom = padding;
        paddingsTypedArray.recycle();
        this.setBackgroundColor(backgroundColor);

        if (this.singleLineEllipsis) {
            TransformationMethod transformationMethod = this.getTransformationMethod();
            this.setSingleLine();
            this.setTransformationMethod(transformationMethod);
        }

        this.initMinBottomLines();
        this.initPadding();
        this.initText();
        this.initFloatingLabel();
        this.initTextWatcher();
        this.checkCharactersCount();
        this.setModo(modo);
        this.setCaps(caps);
        cartaoNaoEncontradoDrawable = getResources().getDrawable(CreditCardPatterns.DEFAULT_NO_MATCH_FOUND_DRAWABLE);
        cartaoNaoEncontradoDrawable.setBounds(0, 0, cartaoNaoEncontradoDrawable.getIntrinsicWidth(), cartaoNaoEncontradoDrawable.getIntrinsicHeight());
        semCartaoDrawable = getResources().getDrawable(android.R.color.transparent);
        semCartaoDrawable.setBounds(0, 0, 5, 5);
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], semCartaoDrawable, getCompoundDrawables()[3]);
        interfaceCartao = new CreditCardPatterns(getContext());
        cartoesDisponiveis = interfaceCartao.mapOfRegexStringAndImageResourceForCreditCardEditText(this);
    }

    public void setCaps(int caps) {
        this.caps = caps;
    }

    public void setModo(int modo) {
        this.modo = modo;
        switch (modo) {
            case MODO_DECIMAL:
                setModoDecimal();
                break;
            case MODO_INTEIRO:
                setModoInteiro();
                break;
            case MODO_DATA:
                setModoData();
                break;
            case MODO_CPF:
                setModoCpf();
                break;
            case MODO_TELEFONE:
                setModoTelefone();
                break;
            case MODO_REAL:
                setModoReal();
                break;
            case MODO_EMAIL:
                setModoEmail();
                break;
            case MODO_TEXTO:
                setModoTexto();
                break;
            case MODO_CNPJ:
                setModoCnpj();
                break;
            case MODO_CARTAO:
                setModoCartao();
                break;
            case MODO_CEP:
                setModoCep();
                break;
            case MODO_MES_ANO:
                setModoMesAno();
                break;
            default:
                setModoTexto();
                break;
        }
    }

    private void setModoInteiro() {
        setRawInputType(InputType.TYPE_CLASS_NUMBER);
        setHelperText(getContext().getString(R.string.somente_inteiros));
        super.addTextChangedListener(watcherInteiro);
        textWatcher = watcherInteiro;
    }

    private void setModoDecimal() {
        setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        setHelperText(getContext().getString(R.string.somente_numeros));
        super.addTextChangedListener(watcherDecimal);
        textWatcher = watcherDecimal;
    }

    private void setModoCpf() {
        setRawInputType(InputType.TYPE_CLASS_NUMBER);
        setHelperText(getContext().getString(R.string.informe_cpf_valido));
        super.addTextChangedListener(watcherCpf);
        textWatcher = watcherCpf;
    }

    private void setModoCnpj() {
        setRawInputType(InputType.TYPE_CLASS_NUMBER);
        setHelperText(getContext().getString(R.string.informe_cnpj_valido));
        super.addTextChangedListener(watcherCnpj);
        textWatcher = watcherCnpj;
    }

    private void setModoTelefone() {
        setRawInputType(InputType.TYPE_CLASS_PHONE);
        setHelperText(getContext().getString(R.string.informe_telefone_ddd));
        super.addTextChangedListener(watcherTelefone);
        textWatcher = watcherTelefone;
    }

    private void setModoReal() {
        setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        setHelperText(getContext().getString(R.string.valor_em_reais));
        super.addTextChangedListener(watcherReal);
        textWatcher = watcherReal;
    }

    private void setModoTexto() {
        super.addTextChangedListener(watcherTexto);
        textWatcher = watcherTexto;
    }

    private void setModoMesAno() {
        dataFormat = new SimpleDateFormat("MM/yy", new Locale("pt", "BR"));
        dataMask = dataMaskMesAno;
        configureData();
    }

    private void setModoData() {
        dataFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
        configureData();
    }

    private void configureData() {
        setRawInputType(InputType.TYPE_CLASS_DATETIME);
        setHelperText(getContext().getString(R.string.informe_uma_data_valida));
        final DatePickerDialog datePickerDialog;
        if (false && getActivity() != null) {
            setFocusable(false);
            setFocusableInTouchMode(false);
            final FragmentManager fm = getActivity().getFragmentManager();
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {


                @Override
                public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                    EditText.super.setText(dataFormat.format(calendar.getTime()));
                    textChanged(dataFormat.format(calendar.getTime()));
                    if (onDateChangeListener != null)
                        onDateChangeListener.onDateChange(calendar.getTime());
                }

                @Override
                public void onDateErased() {
                    setText(null);
                    if (onDateChangeListener != null)
                        onDateChangeListener.onDateChange(null);
                }


            };
            Calendar today = Calendar.getInstance();
            int year = today.get(Calendar.YEAR);
            int month = today.get(Calendar.MONTH);
            int day = today.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = DatePickerDialog.newInstance(dateSetListener, year, month, day);
            datePickerDialog.showYearPickerFirst(true);
            datePickerDialog.setAccentColor(primaryColor);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!datePickerDialog.isAdded())
                        datePickerDialog.show(fm, TAG_DIALOG_DATE);
                }
            });
        } else {
            setRawInputType(InputType.TYPE_CLASS_NUMBER);
            setHelperText(getContext().getString(R.string.informe_uma_data_valida));
            super.addTextChangedListener(watcherData);
            textWatcher = watcherData;
        }
    }

    private void setModoEmail() {
        setHelperText(getContext().getString(R.string.informe_email_valido));
        super.addTextChangedListener(watcherEmail);
        textWatcher = watcherEmail;
    }

    private void setModoCartao() {
        setRawInputType(InputType.TYPE_CLASS_NUMBER);
        setHelperText(getContext().getString(R.string.informe_cartao));
        super.addTextChangedListener(watcherCartao);
        textWatcher = watcherCartao;
    }

    private void setModoCep() {
        setRawInputType(InputType.TYPE_CLASS_NUMBER);
        setHelperText(getContext().getString(R.string.informe_cep_valido));
        super.addTextChangedListener(watcherCep);
        textWatcher = watcherCep;
    }

    TextWatcher watcherCnpj = new TextWatcher() {
        boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!isUpdating) {
                isUpdating = true;
                if (charSequence.length() <= cnpjMask.length()) {
                    String texto = unmask(charSequence.toString());
                    String nova = "";
                    int j;
                    int aux = 0;
                    for (j = 0; j < texto.toCharArray().length; j++) {
                        int k = j + aux;
                        char letra = texto.toCharArray()[j];
                        if (isNumero(letra)) {
                            if (cnpjMask.toCharArray()[k] != '#') {
                                nova += cnpjMask.toCharArray()[k];
                                aux++;
                            }
                            nova += letra;
                        }
                    }
                    EditText.super.setText(nova);
                    setSelection(nova.length());
                } else {
                    String texto = charSequence.toString().substring(0, charSequence.length() - 1);
                    EditText.super.setText(texto);
                    setSelection(texto.length());
                }
                isUpdating = false;
                if (onTextChangeListener != null)
                    textChanged(getText().toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (getText().toString().length() > 0)
                if (ValidaCNPJ.isCNPJ(unmask(getText().toString()))) {
                    setError(null);
                    cnpjValido = true;
                    salvar();
                } else {
                    setError(getContext().getString(R.string.cnpj_invalido));
                    cnpjValido = false;
                }
            else {
                setError(null);
                salvar();
            }
        }
    };

    TextWatcher watcherCpf = new TextWatcher() {
        boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!isUpdating) {
                isUpdating = true;
                if (charSequence.length() <= cpfMask.length()) {
                    String texto = unmask(charSequence.toString());
                    EditText.super.setText(formatarCpf(texto));
                    setSelection(formatarCpf(texto).length());
                } else {
                    String texto = charSequence.toString().substring(0, charSequence.length() - 1);
                    EditText.super.setText(texto);
                    setSelection(texto.length());
                }
                isUpdating = false;
                textChanged(getText().toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (unmask(getText().toString()).length() > 10)
                if (ValidaCPF.isCPF(unmask(getText().toString()))) {
                    setError(null);
                    cpfValido = true;
                    salvar();
                } else {
                    setError(getContext().getString(R.string.cpf_invalido));
                    cpfValido = false;
                }
            else {
                setError(null);
                salvar();
            }
        }
    };

    TextWatcher watcherTelefone = new TextWatcher() {
        boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!isUpdating) {
                isUpdating = true;
                verificaNonoDigito(unmask(charSequence.toString()));
                if (charSequence.length() <= telefoneMask.length()) {
                    String texto = unmask(charSequence.toString());
                    EditText.super.setText(formatarTelefone(texto));
                    setSelection(formatarTelefone(texto).length());
                } else {
                    String texto = charSequence.toString().substring(0, charSequence.length() - 1);
                    EditText.super.setText(texto);
                    setSelection(texto.length());
                }
                textChanged(getText().toString());
                isUpdating = false;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!isNumero(unmask(getText().toString())) && unmask(getText().toString()).length() >= 10) {
                setError(getContext().getString(R.string.informe_telefone_ddd));
            } else {
                salvar();
                setError(null);
                setHelperText(getContext().getString(R.string.informe_telefone_ddd));
            }
        }
    };

    TextWatcher watcherData = new TextWatcher() {
        boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!isUpdating) {
                isUpdating = true;
                if (charSequence.length() <= dataMask.length()) {
                    String texto = unmask(charSequence.toString());
                    String nova = "";
                    int j;
                    int aux = 0;
                    for (j = 0; j < texto.toCharArray().length; j++) {
                        int k = j + aux;
                        char letra = texto.toCharArray()[j];
                        if (isNumero(letra)) {
                            if (dataMask.toCharArray()[k] != '#') {
                                nova += dataMask.toCharArray()[k];
                                aux++;
                            }
                            nova += letra;
                        }
                    }
                    EditText.super.setText(nova);
                    setSelection(nova.length());
                } else {
                    String texto = charSequence.toString().substring(0, charSequence.length() - 1);
                    EditText.super.setText(texto);
                    setSelection(texto.length());
                }
                textChanged(getText().toString());
                isUpdating = false;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (getText().toString().length() > 0)
                if (getText().toString().length() == dataMask.length()) {
                    try {
                        String regex = "^([0-2][0-9]||3[0-1])/(0[0-9]||1[0-2])/([0-9][0-9])?[0-9][0-9]$";
                        Pattern pattern = Pattern.compile(regex);
                        if (!pattern.matcher(getText().toString()).matches())
                            throw new ParseException("", 0);
                        dataFormat.parse(getText().toString());
                        setError(null);
                        salvar();
                    } catch (ParseException e) {
                        setError(getContext().getString(R.string.informe_uma_data_valida));
                    }
                } else {
                    setError(getContext().getString(R.string.informe_uma_data_valida));
                }
            else {
                setError(null);
                salvar();
            }
        }
    };

    TextWatcher watcherInteiro = new TextWatcher() {
        boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!isUpdating) {
                isUpdating = true;
                String texto = "";
                for (char letra : charSequence.toString().toCharArray()) {
                    if (isNumero(letra))
                        texto += letra;
                }
                EditText.super.setText(texto);
                setSelection(texto.length());
                textChanged(getText().toString());
                isUpdating = false;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (getText().toString().length() > 0)
                if (isNumero(unmask(getText().toString()))) {
                    setError(null);
                    salvar();
                } else
                    setError(getContext().getString(R.string.somente_inteiros));
            else {
                salvar();
                setError(null);
            }
        }
    };

    TextWatcher watcherDecimal = new TextWatcher() {
        boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!isUpdating) {
                isUpdating = true;
                boolean temPonto = false;
                String texto = "";
                for (char letra : charSequence.toString().toCharArray()) {
                    if (isNumero(letra))
                        texto += letra;
                    if ((letra == '.' || letra == ',') && !temPonto) {
                        if (texto.equals(""))
                            texto += "0";
                        texto += ",";
                        temPonto = true;
                    }
                }
                EditText.super.setText(texto);
                setSelection(texto.length());
                isUpdating = false;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (getText().toString().length() > 0)
                if (getText().toString().toCharArray()[getText().toString().length() - 1] == '.')
                    setError(getContext().getString(R.string.somente_numeros));
                else if (isNumero(unmask(getText().toString()))) {
                    setError(null);
                    salvar();
                } else
                    setError(getContext().getString(R.string.somente_inteiros));
            else {
                setError(null);
                salvar();
            }
            textChanged(getText().toString());
        }
    };

    TextWatcher watcherReal = new TextWatcher() {
        boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!isUpdating) {
                isUpdating = true;
                String texto = "";
                String digitado = charSequence.toString();
                for (char letra : digitado.toCharArray()) {
                    if (isNumero(letra)) {
                        texto += letra;
                    }
                }
                texto = formatarMonetario(texto);
                EditText.super.setText(texto);
                setSelection(texto.length());
                textChanged(getText().toString());
                isUpdating = false;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (getText().toString().length() > 0)
                if (isNumero(unmask(getText().toString()))) {
                    setError(null);
                    salvar();
                } else
                    setError(getContext().getString(R.string.valor_invalido));
            else {
                setError(null);
                salvar();
            }
        }
    };

    TextWatcher watcherTexto = new TextWatcher() {
        boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int p, int i1, int i2) {
            String texto = charSequence.toString();
            char ultimo = charSequence.toString().isEmpty() ? 'w' : charSequence.charAt(texto.length() - 1);
            if (caps == CAPS_NENHUM)
                texto = texto.toLowerCase();
            if (caps == CAPS_TODOS)
                texto = texto.toUpperCase();
            if (caps == CAPS_PALAVRAS) {
                String[] palavras = texto.isEmpty() ? new String[0] : texto.split("[ ]");
                String[] novas = new String[palavras.length];
                texto = "";
                for (int i = 0; i < palavras.length; i++) {
                    novas[i] = palavras[i].substring(0, 1).toUpperCase() + palavras[i].substring(1);
                }
                for (int i = 0; i < novas.length; i++) {
                    texto += novas[i] + (i + 1 < novas.length ? " " : ultimo == ' ' ? " " : "");
                }
            }
            if (caps == CAPS_SENTENCAS) {
                String[] sentencas = texto.isEmpty() ? new String[0] : texto.split("[.][ ]");
                String[] novas = new String[sentencas.length];
                texto = "";
                for (int i = 0; i < sentencas.length; i++) {
                    novas[i] = sentencas[i].substring(0, 1).toUpperCase() + sentencas[i].substring(1);
                }
                for (int i = 0; i < novas.length; i++) {
                    texto += novas[i] + (i + 1 < novas.length ? ". " : ultimo == ' ' ? " " : "");
                }
            }
            Log.i(getClass().getSimpleName(), "Texto: " + texto + "\t Caps: " + caps);
            if (!isUpdating) {
                isUpdating = true;
                textChanged(texto);
                setError(null);
                isUpdating = false;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            salvar();
        }
    };

    //Created by Edson

    TextWatcher watcherEmail = new TextWatcher() {
        boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String texto = s.toString();
            if (!isUpdating) {
                isUpdating = true;
                textChanged(s.toString());
                EditText.super.setText(texto);
                setSelection(texto.length());
                setError(null);
                isUpdating = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isEmailValid(getText().toString())) {
                salvar();
            } else {
                if (!getText().toString().isEmpty())
                    setError(getContext().getString(R.string.email_invalido));
            }
        }
    };

    TextWatcher watcherCartao = new TextWatcher() {
        boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!isUpdating) {
                isUpdating = true;
                if (charSequence.length() <= cartaoMask.length()) {
                    String texto = unmask(charSequence.toString());
                    String nova = "";
                    int j;
                    int aux = 0;
                    for (j = 0; j < texto.toCharArray().length; j++) {
                        int k = j + aux;
                        char letra = texto.toCharArray()[j];
                        if (isNumero(letra)) {
                            if (cartaoMask.toCharArray()[k] != '#') {
                                nova += cartaoMask.toCharArray()[k];
                                aux++;
                            }
                            nova += letra;
                        }
                    }
                    EditText.super.setText(nova);
                    setSelection(nova.length());
                } else {
                    String texto = charSequence.toString().substring(0, charSequence.length() - 1);
                    EditText.super.setText(texto);
                    setSelection(texto.length());
                }
                isUpdating = false;
                textChanged(getText().toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (getText().toString().length() == cartaoMask.length()) {
                setError(null);
                cpfValido = true;
                salvar();
            } else {
                setError(getContext().getString(R.string.cartao_invalido));
                cartaoValido = false;
            }
            mostraCartaoDrawable();
        }
    };

    TextWatcher watcherCep = new TextWatcher() {
        boolean isUpdating;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!isUpdating) {
                isUpdating = true;
                if (charSequence.length() <= cepMask.length()) {
                    String texto = unmask(charSequence.toString());
                    String nova = "";
                    int j;
                    int aux = 0;
                    for (j = 0; j < texto.toCharArray().length; j++) {
                        int k = j + aux;
                        char letra = texto.toCharArray()[j];
                        if (isNumero(letra)) {
                            if (cepMask.toCharArray()[k] != '#') {
                                nova += cepMask.toCharArray()[k];
                                aux++;
                            }
                            nova += letra;
                        }
                    }
                    EditText.super.setText(nova);
                    setSelection(nova.length());
                } else {
                    String texto = charSequence.toString().substring(0, charSequence.length() - 1);
                    EditText.super.setText(texto);
                    setSelection(texto.length());
                }
                isUpdating = false;
                textChanged(getText().toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (getText().toString().length() == cepMask.length()) {
                setHelperText(getContext().getString(R.string.cep));
                cepValido = true;
                salvar();
            } else {
                setHelperText(getContext().getString(R.string.complete_o_cep));
                cepValido = false;
            }
            setError(null);
        }
    };

    private void mostraCartaoDrawable() {
        Drawable drawable = null;
        if ((getText().toString()).length() < cartaoMask.length()) {
            cartaoSelecionado = null;
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], semCartaoDrawable, getCompoundDrawables()[3]);
        } else {
            for (CreditCard creditCard : cartoesDisponiveis) {
                String regex = creditCard.getRegexPattern();
                if (unmask(getText().toString()).matches(regex)) {
                    cartaoSelecionado = creditCard;
                    drawable = creditCard.getDrawable();
                    break;
                }
            }
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], drawable, getCompoundDrawables()[3]);
            } else {
                cartaoSelecionado = null;
                setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], cartaoNaoEncontradoDrawable, getCompoundDrawables()[3]);
            }
        }
    }

    public void setText(String string) {
        if (string == null || string.isEmpty())
            EditText.super.setText("");
        else if (modo == MODO_REAL) {
            EditText.super.setText(formatarMonetario(String.format("%.2f", Double.parseDouble(string))));
        } else if (modo == MODO_DATA) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
            try {
                EditText.super.setText(dateFormatter.format(dateFormatter.parse(string)));
            } catch (ParseException e) {
                EditText.super.setText("");
                e.printStackTrace();
            }
        } else if (modo == MODO_TEXTO || modo == MODO_EMAIL) {
            EditText.super.setText(string);
        } else if (modo == MODO_CPF) {
            EditText.super.setText(formatarCpf(unmask(string)));
        } else if (modo == MODO_TELEFONE) {
            EditText.super.setText(formatarTelefone(unmask(string)));
        } else if (modo == MODO_INTEIRO) {
            EditText.super.setText(String.format("%d", Integer.parseInt(string)));
        } else if (modo == MODO_DECIMAL) {
            EditText.super.setText(String.format("%.1f", Double.parseDouble(string)));
        }
        textChanged(getText().toString());
    }

    private String unmask(String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "")
                .replaceAll("[/]", "").replaceAll("[(]", "")
                .replaceAll("[)]", "").replaceAll("[,]", "")
                .replaceAll("[R]", "").replaceAll("[$]", "")
                .replaceAll("[ ]", "");
    }

    public String unmask() {
        return unmask(getText().toString());
    }

    private void verificaNonoDigito(String texto) {
        if (unmask(texto).length() == unmask(telefoneMask9Digitos).length()) {
            telefoneMask = telefoneMask9Digitos;
        } else {
            telefoneMask = telefoneMask8Digitos;
        }
    }

    private boolean isNumero(char letra) {
        if (letra == '1' || letra == '2' || letra == '3' || letra == '4' || letra == '5' || letra == '6' || letra == '7' || letra == '8' || letra == '9' || letra == '0' || letra == '-')
            return true;
        return false;
    }

    private boolean isNumero(String texto) {
        for (char letra : texto.toCharArray()) {
            if (!isNumero(letra))
                return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        return !email.equals("") && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void addTextChangedListener(TextWatcher watcher) {
        return;
    }

    private String formatarMonetario(String d) {
        d = unmask(d);
        double value = Double.parseDouble(d) / 100;
        String digitado = new DecimalFormat("#.00").format(value);
        char[] array = new StringBuilder(digitado.substring(0, digitado.length() - 2)).reverse().toString().toCharArray();
        String numero = "";
        for (int i = 0; i < array.length; i++) {
            char letra = array[i];
            if (isNumero(letra)) {
                numero += letra;
                if (i > 0 && i % 3 == 0 && i + 1 < array.length) {
                    numero += ".";
                }
            }
        }
        numero = new StringBuilder(numero).reverse().toString();
        numero += digitado.substring(digitado.length() - 2, digitado.length());
        Log.i(getClass().getSimpleName(), digitado);
        Log.i(getClass().getSimpleName(), numero);
        String valor = numero.replaceFirst("^0+(?!$)", "");
        String texto = "R$ ";
        if (valor.length() == 0) {
            texto += "0,00";
            return texto;
        }
        if (valor.length() == 1) {
            texto += "0,0" + valor;
            return texto;
        }
        if (valor.length() == 2) {
            texto += "0," + valor;
            return texto;
        }
        texto += valor.substring(0, valor.length() - 2) + "," + valor.substring(valor.length() - 2);
        Log.i(getClass().getSimpleName(), texto);
        return texto;
    }

    private String formatarCpf(String digitado) {
        String nova = "";
        int j;
        int aux = 0;
        for (j = 0; j < digitado.toCharArray().length; j++) {
            int k = j + aux;
            char letra = digitado.toCharArray()[j];
            if (isNumero(letra)) {
                if (cpfMask.toCharArray()[k] != '#') {
                    nova += cpfMask.toCharArray()[k];
                    aux++;
                }
                nova += letra;
            }
        }
        return nova;
    }

    private String formatarTelefone(String digitado) {
        verificaNonoDigito(digitado);
        String nova = "";
        int j;
        int aux = 0;
        for (j = 0; j < digitado.toCharArray().length; j++) {
            int k = j + aux;
            char letra = digitado.toCharArray()[j];
            if (isNumero(letra)) {
                if (telefoneMask.toCharArray()[k] != '#') {
                    nova += telefoneMask.toCharArray()[k];
                    aux++;
                }
                nova += letra;
            }
        }
        return nova;
    }

    private Activity getActivity() {
        if (activity != null)
            return activity;
        if (getContext() instanceof Activity)
            return (Activity) getContext();
        return null;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        if (modo == MODO_DATA || modo == MODO_MES_ANO)
            configureData();
    }

    private void initText() {
        if (!TextUtils.isEmpty(this.getText())) {
            Editable text = this.getText();
            super.setText((CharSequence) null);
            this.resetHintTextColor();
            super.setText(text);
            this.setSelection(text.length());
            this.floatingLabelFraction = 1.0F;
            this.floatingLabelShown = true;
        } else {
            this.resetHintTextColor();
        }

        this.resetTextColor();
    }

    private void initTextWatcher() {
        super.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                EditText.this.checkCharactersCount();
                if (EditText.this.autoValidate) {
                    EditText.this.validate();
                } else {
                    EditText.this.setError((CharSequence) null);
                }

                EditText.this.postInvalidate();
            }
        });
    }

    private Typeface getCustomTypeface(@NonNull String fontPath) {
        return Typeface.createFromAsset(this.getContext().getAssets(), fontPath);
    }

    public void setIconLeft(@DrawableRes int res) {
        this.iconLeftBitmaps = this.generateIconBitmaps(res);
        this.initPadding();
    }

    public void setIconLeft(Drawable drawable) {
        this.iconLeftBitmaps = this.generateIconBitmaps(drawable);
        this.initPadding();
    }

    public void setIconLeft(Bitmap bitmap) {
        this.iconLeftBitmaps = this.generateIconBitmaps(bitmap);
        this.initPadding();
    }

    public void setIconRight(@DrawableRes int res) {
        this.iconRightBitmaps = this.generateIconBitmaps(res);
        this.initPadding();
    }

    public void setIconRight(Drawable drawable) {
        this.iconRightBitmaps = this.generateIconBitmaps(drawable);
        this.initPadding();
    }

    public void setIconRight(Bitmap bitmap) {
        this.iconRightBitmaps = this.generateIconBitmaps(bitmap);
        this.initPadding();
    }

    public boolean isCpfValido() {
        return cpfValido;
    }

    public boolean isCnpjValido() {
        return cnpjValido;
    }

    public boolean isCartaoValido() {
        return cartaoValido;
    }

    public boolean isShowClearButton() {
        return this.showClearButton;
    }

    public String getDDD() {
        if (modo != MODO_TELEFONE || unmask(getText().toString()).length() < 2)
            return null;
        return unmask(getText().toString()).substring(0, 2);
    }

    public String getTelefone() {
        if (modo != MODO_TELEFONE || unmask(getText().toString()).trim().length() < 10 || unmask(getText().toString()).trim().length() > 11)
            return null;
        return unmask(getText().toString()).substring(2, unmask(getText().toString()).trim().length());
    }

    public void setShowClearButton(boolean show) {
        this.showClearButton = show;
        this.correctPaddings();
    }

    private Bitmap[] generateIconBitmaps(@DrawableRes int origin) {
        if (origin == -1) {
            return null;
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(this.getResources(), origin, options);
            int size = Math.max(options.outWidth, options.outHeight);
            options.inSampleSize = size > this.iconSize ? size / this.iconSize : 1;
            options.inJustDecodeBounds = false;
            return this.generateIconBitmaps(BitmapFactory.decodeResource(this.getResources(), origin, options));
        }
    }

    private Bitmap[] generateIconBitmaps(Drawable drawable) {
        if (drawable == null) {
            return null;
        } else {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return this.generateIconBitmaps(Bitmap.createScaledBitmap(bitmap, this.iconSize, this.iconSize, false));
        }
    }

    private Bitmap[] generateIconBitmaps(Bitmap origin) {
        if (origin == null) {
            return null;
        } else {
            Bitmap[] iconBitmaps = new Bitmap[4];
            origin = this.scaleIcon(origin);
            iconBitmaps[0] = origin.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(iconBitmaps[0]);
            canvas.drawColor(this.baseColor & 16777215 | (Colors.isLight(this.baseColor) ? -16777216 : -1979711488), PorterDuff.Mode.SRC_IN);
            iconBitmaps[1] = origin.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(iconBitmaps[1]);
            canvas.drawColor(this.primaryColor, PorterDuff.Mode.SRC_IN);
            iconBitmaps[2] = origin.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(iconBitmaps[2]);
            canvas.drawColor(this.baseColor & 16777215 | (Colors.isLight(this.baseColor) ? 1275068416 : 1107296256), PorterDuff.Mode.SRC_IN);
            iconBitmaps[3] = origin.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(iconBitmaps[3]);
            canvas.drawColor(this.errorColor, PorterDuff.Mode.SRC_IN);
            return iconBitmaps;
        }
    }

    private Bitmap scaleIcon(Bitmap origin) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        int size = Math.max(width, height);
        if (size == this.iconSize) {
            return origin;
        } else if (size > this.iconSize) {
            int scaledWidth;
            int scaledHeight;
            if (width > this.iconSize) {
                scaledWidth = this.iconSize;
                scaledHeight = (int) ((float) this.iconSize * ((float) height / (float) width));
            } else {
                scaledHeight = this.iconSize;
                scaledWidth = (int) ((float) this.iconSize * ((float) width / (float) height));
            }

            return Bitmap.createScaledBitmap(origin, scaledWidth, scaledHeight, false);
        } else {
            return origin;
        }
    }

    public float getFloatingLabelFraction() {
        return this.floatingLabelFraction;
    }

    public void setFloatingLabelFraction(float floatingLabelFraction) {
        this.floatingLabelFraction = floatingLabelFraction;
        this.invalidate();
    }

    public float getFocusFraction() {
        return this.focusFraction;
    }

    public void setFocusFraction(float focusFraction) {
        this.focusFraction = focusFraction;
        this.invalidate();
    }

    public float getCurrentBottomLines() {
        return this.currentBottomLines;
    }

    public void setCurrentBottomLines(float currentBottomLines) {
        this.currentBottomLines = currentBottomLines;
        this.initPadding();
    }

    public boolean isFloatingLabelAlwaysShown() {
        return this.floatingLabelAlwaysShown;
    }

    public void setFloatingLabelAlwaysShown(boolean floatingLabelAlwaysShown) {
        this.floatingLabelAlwaysShown = floatingLabelAlwaysShown;
        this.invalidate();
    }

    public boolean isHelperTextAlwaysShown() {
        return this.helperTextAlwaysShown;
    }

    public void setHelperTextAlwaysShown(boolean helperTextAlwaysShown) {
        this.helperTextAlwaysShown = helperTextAlwaysShown;
        this.invalidate();
    }

    @Nullable
    public Typeface getAccentTypeface() {
        return this.accentTypeface;
    }

    public void setAccentTypeface(Typeface accentTypeface) {
        this.accentTypeface = accentTypeface;
        this.textPaint.setTypeface(accentTypeface);
        this.postInvalidate();
    }

    public boolean isHideUnderline() {
        return this.hideUnderline;
    }

    public void setHideUnderline(boolean hideUnderline) {
        this.hideUnderline = hideUnderline;
        this.initPadding();
        this.postInvalidate();
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public void setUnderlineColor(int color) {
        this.underlineColor = color;
        this.postInvalidate();
    }

    public CharSequence getFloatingLabelText() {
        return this.floatingLabelText;
    }

    public void setFloatingLabelText(@Nullable CharSequence floatingLabelText) {
        this.floatingLabelText = floatingLabelText == null ? this.getHint() : floatingLabelText;
        this.postInvalidate();
    }

    public int getFloatingLabelTextSize() {
        return this.floatingLabelTextSize;
    }

    public void setFloatingLabelTextSize(int size) {
        this.floatingLabelTextSize = size;
        this.initPadding();
    }

    public int getFloatingLabelTextColor() {
        return this.floatingLabelTextColor;
    }

    public void setFloatingLabelTextColor(int color) {
        this.floatingLabelTextColor = color;
        this.postInvalidate();
    }

    public int getBottomTextSize() {
        return this.bottomTextSize;
    }

    public void setBottomTextSize(int size) {
        this.bottomTextSize = size;
        this.initPadding();
    }

    private int getPixel(int dp) {
        return dp2px(this.getContext(), (float) dp);
    }

    private int dp2px(Context context, float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }

    private void initPadding() {
        this.extraPaddingTop = this.floatingLabelEnabled ? this.floatingLabelTextSize + this.floatingLabelPadding : this.floatingLabelPadding;
        this.textPaint.setTextSize((float) this.bottomTextSize);
        Paint.FontMetrics textMetrics = this.textPaint.getFontMetrics();
        this.extraPaddingBottom = (int) ((textMetrics.descent - textMetrics.ascent) * this.currentBottomLines) + (this.hideUnderline ? this.bottomSpacing : this.bottomSpacing * 2);
        this.extraPaddingLeft = this.iconLeftBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding;
        this.extraPaddingRight = this.iconRightBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding;
        this.correctPaddings();
    }

    private void initMinBottomLines() {
        boolean extendBottom = this.minCharacters > 0 || this.maxCharacters > 0 || this.singleLineEllipsis || this.tempErrorText != null || this.helperText != null;
        this.currentBottomLines = (float) (this.minBottomLines = this.minBottomTextLines > 0 ? this.minBottomTextLines : (extendBottom ? 1 : 0));
    }

    /**
     * @deprecated
     */
    @Deprecated
    public final void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
    }

    public void setPaddings(int left, int top, int right, int bottom) {
        this.innerPaddingTop = top;
        this.innerPaddingBottom = bottom;
        this.innerPaddingLeft = left;
        this.innerPaddingRight = right;
        this.correctPaddings();
    }

    private void correctPaddings() {
        int buttonsWidthLeft = 0;
        int buttonsWidthRight = 0;
        int buttonsWidth = this.iconOuterWidth * this.getButtonsCount();
        if (this.isRTL()) {
            buttonsWidthLeft = buttonsWidth;
        } else {
            buttonsWidthRight = buttonsWidth;
        }

        super.setPadding(this.innerPaddingLeft + this.extraPaddingLeft + buttonsWidthLeft, this.innerPaddingTop + this.extraPaddingTop, this.innerPaddingRight + this.extraPaddingRight + buttonsWidthRight, this.innerPaddingBottom + this.extraPaddingBottom);
    }

    private int getButtonsCount() {
        return this.isShowClearButton() ? 1 : 0;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.firstShown) {
            this.firstShown = true;
        }

    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            this.adjustBottomLines();
        }

    }

    private boolean adjustBottomLines() {
        if (this.getWidth() == 0) {
            return false;
        } else {
            this.textPaint.setTextSize((float) this.bottomTextSize);
            int destBottomLines;
            if (this.tempErrorText == null && this.helperText == null) {
                destBottomLines = this.minBottomLines;
            } else {
                Layout.Alignment alignment = (this.getGravity() & 5) != 5 && !this.isRTL() ? ((this.getGravity() & 3) == 3 ? Layout.Alignment.ALIGN_NORMAL : Layout.Alignment.ALIGN_CENTER) : Layout.Alignment.ALIGN_OPPOSITE;
                this.textLayout = new StaticLayout(this.tempErrorText != null ? this.tempErrorText : this.helperText, this.textPaint, this.getWidth() - this.getBottomTextLeftOffset() - this.getBottomTextRightOffset() - this.getPaddingLeft() - this.getPaddingRight(), alignment, 1.0F, 0.0F, true);
                destBottomLines = Math.max(this.textLayout.getLineCount(), this.minBottomTextLines);
            }

            try {
                if (this.bottomLines != (float) destBottomLines) {
                    this.getBottomLinesAnimator((float) destBottomLines).start();
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

            this.bottomLines = (float) destBottomLines;
            return true;
        }

    }

    public int getInnerPaddingTop() {
        return this.innerPaddingTop;
    }

    public int getInnerPaddingBottom() {
        return this.innerPaddingBottom;
    }

    public int getInnerPaddingLeft() {
        return this.innerPaddingLeft;
    }

    public int getInnerPaddingRight() {
        return this.innerPaddingRight;
    }

    private void initFloatingLabel() {
        super.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (EditText.this.floatingLabelEnabled) {
                    if (s.length() == 0) {
                        if (EditText.this.floatingLabelShown) {
                            EditText.this.floatingLabelShown = false;
                            EditText.this.getLabelAnimator().reverse();
                        }
                    } else if (!EditText.this.floatingLabelShown) {
                        EditText.this.floatingLabelShown = true;
                        EditText.this.getLabelAnimator().start();
                    }
                }

            }
        });
        this.innerFocusChangeListener = new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (EditText.this.floatingLabelEnabled && EditText.this.highlightFloatingLabel) {
                    if (hasFocus) {
                        EditText.this.getLabelFocusAnimator().start();
                    } else {
                        EditText.this.getLabelFocusAnimator().reverse();
                    }
                }

                if (EditText.this.validateOnFocusLost && !hasFocus) {
                    EditText.this.validate();
                }

                if (EditText.this.outerFocusChangeListener != null) {
                    EditText.this.outerFocusChangeListener.onFocusChange(v, hasFocus);
                }

            }
        };
        super.setOnFocusChangeListener(this.innerFocusChangeListener);
    }

    public boolean isValidateOnFocusLost() {
        return this.validateOnFocusLost;
    }

    public void setValidateOnFocusLost(boolean validate) {
        this.validateOnFocusLost = validate;
    }

    public void setBaseColor(int color) {
        if (this.baseColor != color) {
            this.baseColor = color;
        }

        this.initText();
        this.postInvalidate();
    }

    public void setPrimaryColor(int color) {
        this.primaryColor = color;
        this.postInvalidate();
    }

    public void setMetTextColor(int color) {
        this.textColorStateList = ColorStateList.valueOf(color);
        this.resetTextColor();
    }

    public void setMetTextColor(ColorStateList colors) {
        this.textColorStateList = colors;
        this.resetTextColor();
    }

    private void resetTextColor() {
        if (this.textColorStateList == null) {
            this.textColorStateList = new ColorStateList(new int[][]{{16842910}, EMPTY_STATE_SET}, new int[]{this.baseColor & 16777215 | -553648128, this.baseColor & 16777215 | 1140850688});
            this.setTextColor(this.textColorStateList);
        } else {
            this.setTextColor(this.textColorStateList);
        }

    }

    public void setMetHintTextColor(int color) {
        this.textColorHintStateList = ColorStateList.valueOf(color);
        this.resetHintTextColor();
    }

    public void setMetHintTextColor(ColorStateList colors) {
        this.textColorHintStateList = colors;
        this.resetHintTextColor();
    }

    private void resetHintTextColor() {
        if (this.textColorHintStateList == null) {
            this.setHintTextColor(this.baseColor & 16777215 | 1140850688);
        } else {
            this.setHintTextColor(this.textColorHintStateList);
        }

    }

    private void setFloatingLabelInternal(int mode) {
        switch (mode) {
            case 1:
                this.floatingLabelEnabled = true;
                this.highlightFloatingLabel = false;
                break;
            case 2:
                this.floatingLabelEnabled = true;
                this.highlightFloatingLabel = true;
                break;
            default:
                this.floatingLabelEnabled = false;
                this.highlightFloatingLabel = false;
        }

    }

    public void setFloatingLabel(@FloatingLabelType int mode) {
        this.setFloatingLabelInternal(mode);
        this.initPadding();
    }

    public int getFloatingLabelPadding() {
        return this.floatingLabelPadding;
    }

    public void setFloatingLabelPadding(int padding) {
        this.floatingLabelPadding = padding;
        this.postInvalidate();
    }

    public boolean isFloatingLabelAnimating() {
        return this.floatingLabelAnimating;
    }

    public void setFloatingLabelAnimating(boolean animating) {
        this.floatingLabelAnimating = animating;
    }

    public void setSingleLineEllipsis() {
        this.setSingleLineEllipsis(true);
    }

    public void setSingleLineEllipsis(boolean enabled) {
        this.singleLineEllipsis = enabled;
        this.initMinBottomLines();
        this.initPadding();
        this.postInvalidate();
    }

    public int getMaxCharacters() {
        return this.maxCharacters;
    }

    public void setMaxCharacters(int max) {
        this.maxCharacters = max;
        this.initMinBottomLines();
        this.initPadding();
        this.postInvalidate();
    }

    public int getMinCharacters() {
        return this.minCharacters;
    }

    public void setMinCharacters(int min) {
        this.minCharacters = min;
        this.initMinBottomLines();
        this.initPadding();
        this.postInvalidate();
    }

    public int getMinBottomTextLines() {
        return this.minBottomTextLines;
    }

    public void setMinBottomTextLines(int lines) {
        this.minBottomTextLines = lines;
        this.initMinBottomLines();
        this.initPadding();
        this.postInvalidate();
    }

    public boolean isAutoValidate() {
        return this.autoValidate;
    }

    public void setAutoValidate(boolean autoValidate) {
        this.autoValidate = autoValidate;
        if (autoValidate) {
            this.validate();
        }

    }

    public int getErrorColor() {
        return this.errorColor;
    }

    public void setErrorColor(int color) {
        this.errorColor = color;
        this.postInvalidate();
    }

    public void setHelperText(CharSequence helperText) {
        this.helperText = helperText == null ? null : helperText.toString();
        if (this.adjustBottomLines()) {
            this.postInvalidate();
        }

    }

    public String getHelperText() {
        return this.helperText;
    }

    public int getHelperTextColor() {
        return this.helperTextColor;
    }

    public void setHelperTextColor(int color) {
        this.helperTextColor = color;
        this.postInvalidate();
    }

    public void setError(CharSequence errorText) {
        this.tempErrorText = errorText == null ? null : errorText.toString();
        if (onErrorListener != null && tempErrorText != null)
            onErrorListener.erro();
        if (this.adjustBottomLines()) {
            this.postInvalidate();
        }

    }

    public void setError() {
        setError(getContext().getString(R.string.campo_obrigatorio));
    }

    public CharSequence getError() {
        return this.tempErrorText;
    }

    private boolean isInternalValid() {
        return this.tempErrorText == null && this.isCharactersCountValid();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isValid(String regex) {
        if (regex == null) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(this.getText());
            return matcher.matches();
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean validate(String regex, CharSequence errorText) {
        boolean isValid = this.isValid(regex);
        if (!isValid) {
            this.setError(errorText);
        }

        this.postInvalidate();
        return isValid;
    }

    public boolean validateWith(@NonNull METValidator validator) {
        Editable text = this.getText();
        boolean isValid = validator.isValid(text, text.length() == 0);
        if (!isValid) {
            this.setError(validator.getErrorMessage());
        }

        this.postInvalidate();
        return isValid;
    }

    public boolean validate() {
        if (this.validators != null && !this.validators.isEmpty()) {
            Editable text = this.getText();
            boolean isEmpty = text.length() == 0;
            boolean isValid = true;
            Iterator var4 = this.validators.iterator();

            while (var4.hasNext()) {
                METValidator validator = (METValidator) var4.next();
                isValid = isValid && validator.isValid(text, isEmpty);
                if (!isValid) {
                    this.setError(validator.getErrorMessage());
                    break;
                }
            }

            if (isValid) {
                this.setError((CharSequence) null);
            }

            this.postInvalidate();
            return isValid;
        } else {
            return true;
        }
    }

    public boolean hasValidators() {
        return this.validators != null && !this.validators.isEmpty();
    }

    public EditText addValidator(METValidator validator) {
        if (this.validators == null) {
            this.validators = new ArrayList();
        }

        this.validators.add(validator);
        return this;
    }

    public void clearValidators() {
        if (this.validators != null) {
            this.validators.clear();
        }

    }

    @Nullable
    public List<METValidator> getValidators() {
        return this.validators;
    }

    public void setLengthChecker(METLengthChecker lengthChecker) {
        this.lengthChecker = lengthChecker;
    }

    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        if (this.innerFocusChangeListener == null) {
            super.setOnFocusChangeListener(listener);
        } else {
            this.outerFocusChangeListener = listener;
        }

    }

    private ObjectAnimator getLabelAnimator() {
        if (this.labelAnimator == null) {
            this.labelAnimator = ObjectAnimator.ofFloat(this, "floatingLabelFraction", new float[]{0.0F, 1.0F});
        }

        this.labelAnimator.setDuration(this.floatingLabelAnimating ? 300L : 0L);
        return this.labelAnimator;
    }

    private ObjectAnimator getLabelFocusAnimator() {
        if (this.labelFocusAnimator == null) {
            this.labelFocusAnimator = ObjectAnimator.ofFloat(this, "focusFraction", new float[]{0.0F, 1.0F});
        }

        return this.labelFocusAnimator;
    }

    private ObjectAnimator getBottomLinesAnimator(float destBottomLines) {
        if (this.bottomLinesAnimator == null) {
            this.bottomLinesAnimator = ObjectAnimator.ofFloat(this, "currentBottomLines", new float[]{destBottomLines});
        } else {
            this.bottomLinesAnimator.cancel();
            this.bottomLinesAnimator.setFloatValues(new float[]{destBottomLines});
        }

        return this.bottomLinesAnimator;
    }

    protected void onDraw(@NonNull Canvas canvas) {
        int startX = this.getScrollX() + (this.iconLeftBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding);
        int endX = this.getScrollX() + (this.iconRightBitmaps == null ? this.getWidth() : this.getWidth() - this.iconOuterWidth - this.iconPadding);
        int lineStartY = this.getScrollY() + this.getHeight() - this.getPaddingBottom();
        this.paint.setAlpha(255);
        Bitmap textMetrics;
        int relativeHeight;
        int bottomTextPadding;
//        float scale = getContext().getResources().getDisplayMetrics().density;
        if (this.iconLeftBitmaps != null) {
            textMetrics = this.iconLeftBitmaps[!this.isInternalValid() ? 3 : (!this.isEnabled() ? 2 : (this.hasFocus() ? 1 : 0))];
            relativeHeight = startX - this.iconPadding - this.iconOuterWidth + (this.iconOuterWidth - textMetrics.getWidth()) / 2;
            bottomTextPadding = lineStartY + this.bottomSpacing - this.iconOuterHeight + (this.iconOuterHeight - textMetrics.getHeight()) / 2;
            canvas.drawBitmap(textMetrics, (float) relativeHeight, (float) bottomTextPadding, this.paint);
        }

        if (this.iconRightBitmaps != null) {
            textMetrics = this.iconRightBitmaps[!this.isInternalValid() ? 3 : (!this.isEnabled() ? 2 : (this.hasFocus() ? 1 : 0))];
            relativeHeight = endX + this.iconPadding + (this.iconOuterWidth - textMetrics.getWidth()) / 2;
            bottomTextPadding = lineStartY + this.bottomSpacing - this.iconOuterHeight + (this.iconOuterHeight - textMetrics.getHeight()) / 2;
            canvas.drawBitmap(textMetrics, (float) relativeHeight, (float) bottomTextPadding, this.paint);
        }

        if (this.hasFocus() && this.showClearButton && !TextUtils.isEmpty(this.getText())) {
            this.paint.setAlpha(255);
            int textMetrics1;
            if (this.isRTL()) {
                textMetrics1 = startX;
            } else {
                textMetrics1 = endX - this.iconOuterWidth;
            }

            Bitmap relativeHeight1 = this.clearButtonBitmaps[0];
            textMetrics1 += (this.iconOuterWidth - relativeHeight1.getWidth()) / 2;
            bottomTextPadding = lineStartY + this.bottomSpacing - this.iconOuterHeight + (this.iconOuterHeight - relativeHeight1.getHeight()) / 2;
            canvas.drawBitmap(relativeHeight1, (float) textMetrics1, (float) bottomTextPadding, this.paint);
        }

        float relativeHeight2;
        if (!this.hideUnderline) {
            lineStartY += this.bottomSpacing;
            if (!this.isInternalValid()) {
                this.paint.setColor(this.errorColor);
                canvas.drawRect((float) startX, (float) lineStartY, (float) endX, (float) (lineStartY + this.getPixel(2)), this.paint);
            } else if (!this.isEnabled()) {
                this.paint.setColor(this.underlineColor == -1 ? this.baseColor & 16777215 | 1140850688 : this.underlineColor);
                float textMetrics3 = (float) this.getPixel(1);

                for (relativeHeight2 = 0.0F; relativeHeight2 < (float) this.getWidth(); relativeHeight2 += textMetrics3 * 3.0F) {
                    canvas.drawRect((float) startX + relativeHeight2, (float) lineStartY, (float) startX + relativeHeight2 + textMetrics3, (float) (lineStartY + this.getPixel(1)), this.paint);
                }
            } else if (this.hasFocus()) {
                this.paint.setColor(this.primaryColor);
                canvas.drawRect((float) startX, (float) lineStartY, (float) endX, (float) (lineStartY + this.getPixel(2)), this.paint);
            } else {
                this.paint.setColor(this.underlineColor != -1 ? this.underlineColor : this.baseColor & 16777215 | 503316480);
                canvas.drawRect((float) startX, (float) lineStartY, (float) endX, (float) (lineStartY + this.getPixel(1)), this.paint);
            }
        }

        this.textPaint.setTextSize((float) this.bottomTextSize); //Usar o scale, quando funcionar
        Paint.FontMetrics textMetrics2 = this.textPaint.getFontMetrics();
        relativeHeight2 = -textMetrics2.ascent - textMetrics2.descent;
        float bottomTextPadding1 = (float) this.bottomTextSize + textMetrics2.ascent + textMetrics2.descent;
        if (this.hasFocus() && this.hasCharactersCounter() || !this.isCharactersCountValid()) {
            this.textPaint.setColor(this.isCharactersCountValid() ? this.baseColor & 16777215 | 1140850688 : this.errorColor);
            String startY = this.getCharactersCounterText();
            canvas.drawText(startY, this.isRTL() ? (float) startX : (float) endX - this.textPaint.measureText(startY), (float) (lineStartY + this.bottomSpacing) + relativeHeight2, this.textPaint);
        }

        if (this.textLayout != null && (this.tempErrorText != null || (this.helperTextAlwaysShown || this.hasFocus()) && !TextUtils.isEmpty(this.helperText))) {
            this.textPaint.setColor(this.tempErrorText != null ? this.errorColor : (this.helperTextColor != -1 ? this.helperTextColor : this.baseColor & 16777215 | 1140850688));
            canvas.save();
            if (this.isRTL()) {
                canvas.translate((float) (endX - this.textLayout.getWidth()), (float) (lineStartY + this.bottomSpacing) - bottomTextPadding1);
            } else {
                canvas.translate((float) (startX + this.getBottomTextLeftOffset()), (float) (lineStartY + this.bottomSpacing) - bottomTextPadding1);
            }

            this.textLayout.draw(canvas);
            canvas.restore();
        }

        int ellipsisStartX;
        int signum;
        float startY1;
        if (this.floatingLabelEnabled && !TextUtils.isEmpty(this.floatingLabelText)) {
            this.textPaint.setTextSize((float) this.floatingLabelTextSize); //Usar o scale, quando funcionar
            this.textPaint.setColor(/*(Integer) this.focusEvaluator.evaluate(this.focusFraction, this.floatingLabelTextColor != -1 ? this.floatingLabelTextColor : this.baseColor & 16777215 | 1140850688, this.primaryColor)*/floatingLabelTextColor);
            startY1 = this.textPaint.measureText(this.floatingLabelText.toString());
            if ((this.getGravity() & 5) != 5 && !this.isRTL()) {
                if ((this.getGravity() & 3) == 3) {
                    ellipsisStartX = startX;
                } else {
                    ellipsisStartX = startX + (int) ((float) this.getInnerPaddingLeft() + ((float) (this.getWidth() - this.getInnerPaddingLeft() - this.getInnerPaddingRight()) - startY1) / 2.0F);
                }
            } else {
                ellipsisStartX = (int) ((float) endX - startY1);
            }

            signum = this.floatingLabelPadding;
            int floatingLabelStartY = (int) ((float) (this.innerPaddingTop + this.floatingLabelTextSize + this.floatingLabelPadding) - (float) signum * (this.floatingLabelAlwaysShown ? 1.0F : this.floatingLabelFraction) + (float) this.getScrollY());
            int alpha = (int) ((this.floatingLabelAlwaysShown ? 1.0F : this.floatingLabelFraction) * 255.0F * (0.74F * this.focusFraction + 0.26F) * (this.floatingLabelTextColor != -1 ? 1.0F : (float) Color.alpha(this.floatingLabelTextColor) / 256.0F));
            this.textPaint.setAlpha(Float.valueOf(floatingLabelFraction * 255.0F).intValue());
            canvas.drawText(this.floatingLabelText.toString(), (float) ellipsisStartX, (float) floatingLabelStartY, this.textPaint);
        }

        if (this.hasFocus() && this.singleLineEllipsis && this.getScrollX() != 0) {
            this.paint.setColor(this.isInternalValid() ? this.primaryColor : this.errorColor);
            startY1 = (float) (lineStartY + this.bottomSpacing);
            if (this.isRTL()) {
                ellipsisStartX = endX;
            } else {
                ellipsisStartX = startX;
            }

            signum = this.isRTL() ? -1 : 1;
            canvas.drawCircle((float) (ellipsisStartX + signum * this.bottomEllipsisSize / 2), startY1 + (float) (this.bottomEllipsisSize / 2), (float) (this.bottomEllipsisSize / 2), this.paint);
            canvas.drawCircle((float) (ellipsisStartX + signum * this.bottomEllipsisSize * 5 / 2), startY1 + (float) (this.bottomEllipsisSize / 2), (float) (this.bottomEllipsisSize / 2), this.paint);
            canvas.drawCircle((float) (ellipsisStartX + signum * this.bottomEllipsisSize * 9 / 2), startY1 + (float) (this.bottomEllipsisSize / 2), (float) (this.bottomEllipsisSize / 2), this.paint);
        }

        super.onDraw(canvas);
    }

    @TargetApi(17)
    private boolean isRTL() {
        if (Build.VERSION.SDK_INT < 17) {
            return false;
        } else {
            Configuration config = this.getResources().getConfiguration();
            return config.getLayoutDirection() == 1;
        }
    }

    private int getBottomTextLeftOffset() {
        return this.isRTL() ? this.getCharactersCounterWidth() : this.getBottomEllipsisWidth();
    }

    private int getBottomTextRightOffset() {
        return this.isRTL() ? this.getBottomEllipsisWidth() : this.getCharactersCounterWidth();
    }

    private int getCharactersCounterWidth() {
        return this.hasCharactersCounter() ? (int) this.textPaint.measureText(this.getCharactersCounterText()) : 0;
    }

    private int getBottomEllipsisWidth() {
        return this.singleLineEllipsis ? this.bottomEllipsisSize * 5 + this.getPixel(4) : 0;
    }

    private void checkCharactersCount() {
        if ((this.firstShown || this.checkCharactersCountAtBeginning) && this.hasCharactersCounter()) {
            Editable text = this.getText();
            int count = text == null ? 0 : this.checkLength(text);
            this.charactersCountValid = count >= this.minCharacters && (this.maxCharacters <= 0 || count <= this.maxCharacters);
        } else {
            this.charactersCountValid = true;
        }

    }

    public boolean isCharactersCountValid() {
        return this.charactersCountValid;
    }

    private boolean hasCharactersCounter() {
        return this.minCharacters > 0 || this.maxCharacters > 0;
    }

    private String getCharactersCounterText() {
        String text;
        if (this.minCharacters <= 0) {
            text = this.isRTL() ? this.maxCharacters + " / " + this.checkLength(this.getText()) : this.checkLength(this.getText()) + " / " + this.maxCharacters;
        } else if (this.maxCharacters <= 0) {
            text = this.isRTL() ? "+" + this.minCharacters + " / " + this.checkLength(this.getText()) : this.checkLength(this.getText()) + " / " + this.minCharacters + "+";
        } else {
            text = this.isRTL() ? this.maxCharacters + "-" + this.minCharacters + " / " + this.checkLength(this.getText()) : this.checkLength(this.getText()) + " / " + this.minCharacters + "-" + this.maxCharacters;
        }

        return text;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.singleLineEllipsis && this.getScrollX() > 0 && event.getAction() == 0 && event.getX() < (float) this.getPixel(20) && event.getY() > (float) (this.getHeight() - this.extraPaddingBottom - this.innerPaddingBottom) && event.getY() < (float) (this.getHeight() - this.innerPaddingBottom)) {
            this.setSelection(0);
            return false;
        } else {
            if (this.hasFocus() && this.showClearButton) {
                switch (event.getAction()) {
                    case 0:
                        if (this.insideClearButton(event)) {
                            this.clearButtonTouched = true;
                            this.clearButtonClicking = true;
                            return true;
                        }
                    case 2:
                        if (this.clearButtonClicking && !this.insideClearButton(event)) {
                            this.clearButtonClicking = false;
                        }

                        if (this.clearButtonTouched) {
                            return true;
                        }
                        break;
                    case 1:
                        if (this.clearButtonClicking) {
                            if (!TextUtils.isEmpty(this.getText())) {
                                super.setText((CharSequence) null);
                            }

                            this.clearButtonClicking = false;
                        }

                        if (this.clearButtonTouched) {
                            this.clearButtonTouched = false;
                            return true;
                        }

                        this.clearButtonTouched = false;
                        break;
                    case 3:
                        this.clearButtonTouched = false;
                        this.clearButtonClicking = false;
                }
            }

            return super.onTouchEvent(event);
        }
    }

    private boolean insideClearButton(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int startX = this.getScrollX() + (this.iconLeftBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding);
        int endX = this.getScrollX() + (this.iconRightBitmaps == null ? this.getWidth() : this.getWidth() - this.iconOuterWidth - this.iconPadding);
        int buttonLeft;
        if (this.isRTL()) {
            buttonLeft = startX;
        } else {
            buttonLeft = endX - this.iconOuterWidth;
        }

        int buttonTop = this.getScrollY() + this.getHeight() - this.getPaddingBottom() + this.bottomSpacing - this.iconOuterHeight;
        return x >= (float) buttonLeft && x < (float) (buttonLeft + this.iconOuterWidth) && y >= (float) buttonTop && y < (float) (buttonTop + this.iconOuterHeight);
    }

    private int checkLength(CharSequence text) {
        return this.lengthChecker == null ? text.length() : this.lengthChecker.getLength(text);
    }

    public void setDouble(double value) {
        String text = "";
        if (modo == MODO_INTEIRO)
            text = String.valueOf((int) value);
        if (modo == MODO_DECIMAL)
            text = String.valueOf(Double.parseDouble(String.valueOf(value))).replaceAll("[.]", ",");
        if (modo == MODO_REAL)
            text = formatarMonetario(String.format(Locale.US, "%.2f", value));
        super.setText(text);
    }

    public void setInteger(int value) {
        super.setText(String.valueOf(value));
    }

    public void setData(Date data) {
        String dateString = null;
        if (data != null)
            dateString = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")).format(data);
        setText(dateString);
    }

    public abstract static class OnTextChangeListener {
        public void onTextChange(String text) {
        }

        public void onTextChange(EditText editText) {
        }
    }

    public abstract static class OnSalvarListener {
        public void salvar() {
        }

        ;

        public void salvar(EditText editText) {
        }
    }

    public abstract static class OnDateChangeListener {
        public void onDateChange(Date date) {
        }
    }

    public interface OnErrorListener {
        public void erro();
    }

    private void textChanged(String text) {
        if (onTextChangeListener != null) {
            onTextChangeListener.onTextChange(text);
            onTextChangeListener.onTextChange(this);
        }
    }

    private void salvar() {
        if (onSalvarListener != null) {
            onSalvarListener.salvar(this);
            onSalvarListener.salvar();
        }
    }

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }

    public void setOnSalvarListener(OnSalvarListener onSalvarListener) {
        this.onSalvarListener = onSalvarListener;
    }

    public void setOnDateChangeListener(OnDateChangeListener onDateChangeListener) {
        this.onDateChangeListener = onDateChangeListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public OnTextChangeListener getOnTextChangeListener() {
        return onTextChangeListener;
    }

    public Integer getInteger() {
        if (modo == MODO_INTEIRO)
            if (getText().toString().trim().equals(""))
                return 0;
            else
                return Integer.parseInt(getText().toString());
        if (modo == MODO_DECIMAL)
            if (getText().toString().trim().equals(""))
                return 0;
            else
                return ((int) Double.parseDouble(getText().toString().replaceAll("[,]", ".")));
        if (modo == MODO_REAL)
            if (getText().toString().trim().equals(""))
                return 0;
            else
                return ((int) Double.parseDouble(getText().toString().replaceAll("[.]", "").replaceAll("[,]", ".")
                        .replaceAll("[R]", "").replaceAll("[$]", "")));
        return null;
    }

    public Double getDouble() {
        if (modo == MODO_INTEIRO)
            if (getText().toString().trim().equals(""))
                return 0D;
            else
                return Double.parseDouble(getText().toString());
        if (modo == MODO_DECIMAL)
            if (getText().toString().trim().equals(""))
                return 0D;
            else
                return Double.parseDouble(getText().toString().replaceAll("[,]", "."));
        if (modo == MODO_REAL)
            if (getText().toString().trim().equals(""))
                return 0D;
            else
                return Double.parseDouble(getText().toString().replaceAll("[.]", "").replaceAll("[,]", ".")
                        .replaceAll("[R]", "").replaceAll("[$]", ""));
        return null;
    }

    public Date getDate() {
        if (modo == MODO_DATA) {
            Date date = null;
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
            try {
                date = formatter.parse(getText().toString());
            } catch (ParseException ignored) {
            }
            return date;
        }
        return null;
    }

    public @interface FloatingLabelType {

    }


}

class Colors {
    public static boolean isLight(int color) {
        return Math.sqrt(
                Color.red(color) * Color.red(color) * .241 +
                        Color.green(color) * Color.green(color) * .691 +
                        Color.blue(color) * Color.blue(color) * .068) > 130;
    }
}

abstract class METValidator {

    /**
     * Error message that the view will display if validation fails.
     * <p>
     * This is protected, so you can change this dynamically in your {@link #isValid(CharSequence, boolean)}
     * implementation. If necessary, you can also interact com this via its getter and setter.
     */
    protected String errorMessage;

    public METValidator(@NonNull String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setErrorMessage(@NonNull String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @NonNull
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Abstract method to implement your own validation checking.
     *
     * @param text    The CharSequence representation of the text in the EditText field. Cannot be null, but may be empty.
     * @param isEmpty Boolean indicating whether or not the text param is empty
     * @return True if valid, false if not
     */
    public abstract boolean isValid(@NonNull CharSequence text, boolean isEmpty);

}

abstract class METLengthChecker {

    public abstract int getLength(CharSequence text);

}
