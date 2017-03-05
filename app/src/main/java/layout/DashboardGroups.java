package layout;

import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.example.lexlevi.sweapp.Adapters.GroupAdapter;
import com.example.lexlevi.sweapp.Common.URLs;
import com.example.lexlevi.sweapp.Controllers.ChatServerAPI;
import com.example.lexlevi.sweapp.Models.Course;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.Models.User;
import com.example.lexlevi.sweapp.R;
import com.example.lexlevi.sweapp.Singletons.UserSession;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DashboardGroups extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private View _rootView;
    private GridView _groupsView;

    public DashboardGroups() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DashboardGroups newInstance(int sectionNumber) {
        DashboardGroups fragment = new DashboardGroups();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _rootView = inflater.inflate(R.layout.fragment_dashboard_groups, container, false);
        _groupsView = (GridView) _rootView.findViewById(R.id.group_gridview);
        loadGroups(_groupsView);
        return _rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGroups(_groupsView);
    }

    protected void loadGroups(final GridView view) {
        final ArrayList<Group> groups = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ChatServerAPI chatServerAPI = retrofit.create(ChatServerAPI.class);
        Call<List<Group>> call = chatServerAPI.getGroupListForUser(UserSession
                .getInstance()
                .getCurrentUser()
                .getId());
        call.enqueue(new Callback<List<Group>>() {
            Snackbar s;
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.body() != null) {
                    for(Group g : response.body()) {
                        groups.add(g);
                    }
                }
                GroupAdapter booksAdapter = new GroupAdapter(getContext(),
                        groups.toArray(new Group[groups.size()]));
                view.setAdapter(booksAdapter);
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                //..
            }
        });
    }
}
