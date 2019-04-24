package br.agr.terras.materialdroid.utils.creditcard;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import br.agr.terras.materialdroid.EditText;
import br.agr.terras.materialdroid.R;

/**
 * Created by leo on 12/09/16.
 */
public class CreditCardPatterns implements CreditCardInterface {
    public static final int DEFAULT_NO_MATCH_FOUND_DRAWABLE = R.drawable.credit_card;

    private Context mContext;

    public CreditCardPatterns(Context context) {
        mContext = context;
    }

    @Override
    public List<CreditCard> mapOfRegexStringAndImageResourceForCreditCardEditText(EditText creditCardEditText) {
        List<CreditCard> listOfPatterns = new ArrayList<CreditCard>();
        CreditCard visa = new CreditCard("^4[0-9]{12}(?:[0-9]{3})?$", mContext.getResources().getDrawable(R.drawable.visa), CreditCardTypeEnum.VISA.cartType);
        CreditCard mastercard = new CreditCard("^5[1-5][0-9]{14}$", mContext.getResources().getDrawable(R.drawable.mastercard), CreditCardTypeEnum.MASTER_CARD.cartType);
        CreditCard elo = new CreditCard("^5[0][0-9]{14}$", mContext.getResources().getDrawable(R.drawable.elo), CreditCardTypeEnum.ELO.cartType);
        CreditCard amex = new CreditCard("^3[47][0-9]{13}$", mContext.getResources().getDrawable(R.drawable.amex), CreditCardTypeEnum.AMERICAN_EXPRESS.cartType);

        listOfPatterns.add(visa);
        listOfPatterns.add(mastercard);
        listOfPatterns.add(elo);
        listOfPatterns.add(amex);
        return listOfPatterns;
    }
}
