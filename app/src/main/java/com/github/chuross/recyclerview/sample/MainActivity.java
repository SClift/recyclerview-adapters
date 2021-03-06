package com.github.chuross.recyclerview.sample;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chuross.recyclerviewadapters.CompositeRecyclerAdapter;
import com.github.chuross.recyclerviewadapters.DividerItemDecorationBuilder;
import com.github.chuross.recyclerviewadapters.GridPaddingItemDecorationBuilder;
import com.github.chuross.recyclerviewadapters.ItemAdapter;
import com.github.chuross.recyclerviewadapters.OnItemClickListener;
import com.github.chuross.recyclerviewadapters.OnItemDoubleClickListener;
import com.github.chuross.recyclerviewadapters.OnItemLongPressedListener;
import com.github.chuross.recyclerviewadapters.SpanSizeLookupBuilder;
import com.github.chuross.recyclerviewadapters.DragItemTouchHelperBuilder;
import com.github.chuross.recyclerviewadapters.ViewItem;
import com.github.chuross.recyclerviewadapters.ViewItemAdapter;
import com.github.chuross.rx.RxItemAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.processors.BehaviorProcessor;


public class MainActivity extends AppCompatActivity {

    private static final int SPAN_SIZE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CompositeRecyclerAdapter compositeAdapter = new CompositeRecyclerAdapter();

        final ItemAdapter<String, RecyclerView.ViewHolder> itemAdapter1 = new ItemAdapter<String, RecyclerView.ViewHolder>(this) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemViewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new RecyclerView.ViewHolder(inflater.inflate(R.layout.item_adapter_1, parent, false)) {
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView.findViewById(R.id.text)).setText(get(position));
            }

            @Override
            public int getAdapterId() {
                return R.layout.item_adapter_1;
            }
        };
        itemAdapter1.add("itemAdapter1#0");
        itemAdapter1.setOnItemClickListener(new OnItemClickListener<String>() {
            @Override
            public void onItemClicked(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull String item) {
                Toast.makeText(holder.itemView.getContext(), "click! adapter1:: " + item, Toast.LENGTH_SHORT).show();
            }
        });
        itemAdapter1.setOnItemLongPressListener(new OnItemLongPressedListener<String>() {
            @Override
            public void onItemLongPressed(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull String item) {
                Toast.makeText(holder.itemView.getContext(), "long press! adapter1:: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        final BehaviorProcessor<List<String>> itemAdapter2Items = BehaviorProcessor.<List<String>>createDefault(new ArrayList<>(Arrays.asList("itemAdapter2#0")));
        final RxItemAdapter<String, RecyclerView.ViewHolder> itemAdapter2 = new RxItemAdapter<String, RecyclerView.ViewHolder>(this, itemAdapter2Items) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemViewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new RecyclerView.ViewHolder(inflater.inflate(R.layout.item_adapter_2, parent, false)) {
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView.findViewById(R.id.text)).setText(get(position));
            }

            @Override
            public int getAdapterId() {
                return R.layout.item_adapter_2;
            }
        };
        itemAdapter2.setOnItemClickListener(new OnItemClickListener<String>() {
            @Override
            public void onItemClicked(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull String item) {
                Toast.makeText(holder.itemView.getContext(), "click! adapter2:: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        // same as itemAdapter1 layout resource
        final ItemAdapter3 itemAdapter3 = new ItemAdapter3(this);
        itemAdapter3.add("itemAdapter3# same layout as itemAdapter1 [draggable] #1");
        itemAdapter3.add("itemAdapter3# same layout as itemAdapter1 [draggable] #2");
        itemAdapter3.add("itemAdapter3# same layout as itemAdapter1 [draggable] #3");

        final ViewItem viewItem1 = new ViewItem(this, R.layout.item_hello_world);
        ViewItem visibleChangeButton = new ViewItem(this, R.layout.visible_toggle, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewItem1.setVisible(!viewItem1.isVisible());
            }
        });
        ViewItem viewItem2 = new AppendButtonViewItem(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemAdapter1.add("itemAdapter1#" + String.valueOf(itemAdapter1.getItemCount()));
                itemAdapter1.add("itemAdapter1#" + String.valueOf(itemAdapter1.getItemCount()));
            }
        });
        ViewItem viewItem3 = new AppendButtonViewItem(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String value = "RxItemAdapter2#" + String.valueOf(itemAdapter2.getItemCount());
                itemAdapter2Items.getValue().add(value);
                itemAdapter2Items.onNext(itemAdapter2Items.getValue());
            }
        });

        final ViewItemAdapter viewItemAdapter = new ViewItemAdapter(this);

        final ViewItem viewItem4 = new ViewItem(this, R.layout.item_footer_1);
        final ViewItem viewItem5 = new ViewItem(this, R.layout.item_footer_2);
        final ViewItem viewItem6 = new ViewItem(this, R.layout.item_append_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewItemAdapter.addAll(
                        viewItem4.clone(),
                        viewItem5.clone()
                );
            }
        });

        viewItemAdapter.add(viewItem4);
        viewItemAdapter.add(viewItem5);

        /*
        compositeAdapter.addAll(
            viewItem1,
            itemAdapter1,
            viewItem2,
            ...
        );
         */
        compositeAdapter.add(viewItem1);
        compositeAdapter.add(visibleChangeButton);
        compositeAdapter.add(itemAdapter1);
        compositeAdapter.add(viewItem2);
        compositeAdapter.add(itemAdapter2);
        compositeAdapter.add(viewItem3);
        compositeAdapter.add(itemAdapter3);
        compositeAdapter.add(viewItemAdapter);
        compositeAdapter.add(viewItem6);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

        /*
         * simple LinearLayoutManager example
         */
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));


        /*
         * Use SpanSizeLookupBuilder, if you want to use GridLayoutManager with SpanSizeLookup.
         */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, SPAN_SIZE);
        recyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new SpanSizeLookupBuilder(compositeAdapter)
                .register(viewItem1, SPAN_SIZE)
                .register(visibleChangeButton, SPAN_SIZE)
                .register(AppendButtonViewItem.class, SPAN_SIZE)
                .register(viewItem6, SPAN_SIZE)
                .register(viewItemAdapter, SPAN_SIZE)
                .register(itemAdapter1, 2)
                .register(itemAdapter3, SPAN_SIZE)
                .build());

        /*
         * grid padding support
         */
        int padding = getResources().getDimensionPixelSize(R.dimen.padding);
        recyclerView.addItemDecoration(new GridPaddingItemDecorationBuilder(compositeAdapter, padding, SPAN_SIZE)
                .paddingType(GridPaddingItemDecorationBuilder.PaddingType.BOTH)
                .register(visibleChangeButton)
                .register(itemAdapter2)
                .register(AppendButtonViewItem.class)
                .build());

        /*
         * divider decoration support
         */
        int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);

        recyclerView.addItemDecoration(new DividerItemDecorationBuilder(compositeAdapter)
                .dividerHeight(dividerHeight)
                .dividerColor(Color.BLACK)
                .register(itemAdapter1)
                .register(AppendButtonViewItem.class)
                .build());
        recyclerView.addItemDecoration(new DividerItemDecorationBuilder(compositeAdapter)
                .dividerHeight(dividerHeight)
                .dividerColor(Color.BLUE)
                .register(visibleChangeButton)
                .register(itemAdapter3)
                .build());

        /*
         * Vertical Drag support
         */
        ItemTouchHelper dragHelper = new DragItemTouchHelperBuilder(compositeAdapter)
                .dragFlag(ItemTouchHelper.UP)
                .dragFlag(ItemTouchHelper.DOWN)
                .register(ItemAdapter3.class)
                .build();
        dragHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(compositeAdapter);
    }

    private static class AppendButtonViewItem extends ViewItem {

        public AppendButtonViewItem(@NonNull Context context, @Nullable View.OnClickListener clickListener) {
            super(context, R.layout.item_append_button, clickListener);
        }
    }

    private static class ItemAdapter3 extends ItemAdapter<String, RecyclerView.ViewHolder> {

        public ItemAdapter3(@NonNull Context context) {
            super(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemViewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new RecyclerView.ViewHolder(inflater.inflate(R.layout.item_adapter_1, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TextView) holder.itemView.findViewById(R.id.text)).setText(get(position));
        }

        // Don't worry! This ItemAdapter will be used recycled 'R.layout.item_adapter_1' ViewHolder, if you want to use same AdapterId.
        @Override
        public int getAdapterId() {
            return R.layout.item_adapter_1;
        }
    }
}
