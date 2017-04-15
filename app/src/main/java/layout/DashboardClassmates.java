package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lexlevi.sweapp.Adapters.ClassmatesAdapter;
import com.example.lexlevi.sweapp.Models.Classmate;
import com.example.lexlevi.sweapp.R;
import com.example.lexlevi.sweapp.Singletons.Client;
import com.example.lexlevi.sweapp.Singletons.Session;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardClassmates extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<Classmate> _classmates;
    private String _param1;
    private String _param2;

    // Views
    private View _rootView;
    private RecyclerView _recyclerView;
    private AVLoadingIndicatorView _avi;

    private OnFragmentInteractionListener _listener;

    public DashboardClassmates() {
        // Required empty public constructor
    }

    public static DashboardClassmates newInstance(String param1, String param2) {
        DashboardClassmates fragment = new DashboardClassmates();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _param1 = getArguments().getString(ARG_PARAM1);
            _param2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _classmates = new ArrayList<>();
        _rootView = inflater.inflate(R.layout.fragment_dashboard_classmates, container, false);
        _recyclerView = (RecyclerView) _rootView.findViewById(R.id.classmates_recyclerview);
        _avi = (AVLoadingIndicatorView) _rootView.findViewById(R.id.classmates_avi);
        _avi.show();
        ClassmatesAdapter classmatesAdapter = new ClassmatesAdapter(_recyclerView, getContext(),
                _classmates.toArray(new Classmate[_classmates.size()]), _recyclerView);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _recyclerView.setAdapter(classmatesAdapter);
        loadClassmates(_recyclerView);
        return _rootView;
    }

    private void loadClassmates(final RecyclerView recyclerView) {
        _classmates = new ArrayList<>();

        Call<List<Classmate>> getMatches = Client
                .shared()
                .api()
                .getMatchingUsersForId(Session
                        .shared()
                        .user()
                        .getId());
        getMatches.enqueue(new Callback<List<Classmate>>() {
            @Override
            public void onResponse(Call<List<Classmate>> call, Response<List<Classmate>> response) {
                for(Classmate c : response.body()) {
                    _classmates.add(c);
                }
                ClassmatesAdapter classmatesAdapter = new ClassmatesAdapter(_recyclerView, getContext(),
                        _classmates.toArray(new Classmate[_classmates.size()]), _recyclerView);
                recyclerView.setAdapter(classmatesAdapter);
                _avi.hide();
            }

            @Override
            public void onFailure(Call<List<Classmate>> call, Throwable t) {
                _avi.hide();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            _listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    public interface OnFragmentInteractionListener {
    }
}
