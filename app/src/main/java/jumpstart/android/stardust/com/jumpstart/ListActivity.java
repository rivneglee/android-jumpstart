package jumpstart.android.stardust.com.jumpstart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView recyclerView = (RecyclerView)this.findViewById(R.id.list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SearchResultsAdapter adapter = new SearchResultsAdapter();

        Flowable.just(R.raw.data_set)
                .map(id -> getResources().openRawResource(id))
                .map(inputStream -> new InputStreamReader(inputStream))
                .map(reader -> new Gson().fromJson(reader, new TypeToken<ArrayList<Flight>>(){}.getType()))
                .subscribe(o -> {
                    if (o instanceof ArrayList) {
                        adapter.setData((ArrayList<Flight>) o);
                    }
                });

        recyclerView.setAdapter(adapter);
    }

    private class Flight {

        private final String start;
        private final String end;

        Flight(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }
    }

    private class SearchResultsAdapter extends RecyclerView.Adapter<FlightViewHolder> {
        private List<Flight> data;

        @Override
        public FlightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout, parent, false);
            return new FlightViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FlightViewHolder holder, int position) {
            Flight flight = data.get(position);
            holder.from.setText(flight.getStart());
            holder.to.setText(flight.getEnd());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setData(List<Flight> data) {
            this.data = data;
        }
    }

    private class FlightViewHolder extends RecyclerView.ViewHolder {

        private final TextView from;
        private final TextView to;

        public FlightViewHolder(final View itemView) {
            super(itemView);
            from = (TextView) itemView.findViewById(R.id.from_text);
            to = (TextView) itemView.findViewById(R.id.to_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), MainActivity.class);
                intent.putExtra("from", from.getText());
                intent.putExtra("to", to.getText());
                itemView.getContext().startActivities(new Intent[]{ intent });
                }
            });
        }
    }
}
