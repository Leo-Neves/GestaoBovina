package pet.sansa.gestaobovina;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.agr.terras.aurora.log.Logger;

public class SyncUsuario {
    private FirebaseAuth auth;
    private FirebaseUser user;

    public SyncUsuario(FirebaseAuth auth){
        this.auth = auth;
    }

    public void autenticar(final FirebaseListener listener){
        user = auth.getCurrentUser();
        if (user==null){
            auth.signInAnonymously().addOnCompleteListener(MyApplication.getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Logger.d("signInAnonymously:success");
                        user = auth.getCurrentUser();
                        listener.sucesso();
                    } else {
                        // If sign in fails, display a message to the user.
                        Logger.w( "signInAnonymously:failure");
                        listener.falha("Falha de conexão");
                    }
                }
            });
        }else{
            listener.sucesso();
        }
    }


}
