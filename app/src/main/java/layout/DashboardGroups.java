package layout;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.lexlevi.sweapp.Adapters.GroupAdapter;
import com.example.lexlevi.sweapp.Models.Group;
import com.example.lexlevi.sweapp.R;
import com.example.lexlevi.sweapp.Singletons.ChatServerClient;
import com.example.lexlevi.sweapp.Singletons.UserSession;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardGroups extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private View _rootView;
    private GridView _groupsView;

    public DashboardGroups() {

    }

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
        Call<List<Group>> call = ChatServerClient
                .getInstance()
                .api()
                .getGroupListForUser(UserSession
                    .getInstance()
                    .getCurrentUser()
                    .getId());
        call.enqueue(new Callback<List<Group>>() {
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