package app.adie.reservation.Fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.adie.reservation.R;

/**
 * Created by Adie on 27/05/2016.
 */
public class DialogLunasFragment extends Fragment {
    private TextView txtkode;
    private static String kod;
    public static DialogLunasFragment newInstance(String kode) {
        kod = kode;
        return new DialogLunasFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_lunas,container,false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.txtkode=(TextView)getView().findViewById(R.id.txtkod);


        txtkode.setText(kod);



    }

}
