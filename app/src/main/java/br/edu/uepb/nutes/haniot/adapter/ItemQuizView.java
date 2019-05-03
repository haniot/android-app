package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemQuizView extends RecyclerView {

    ItemAdapter itemAdapter;
    ItemQuizView recyclerView;

    public ItemQuizView(Context context, AttributeSet attrs) {
        super(context);
        ButterKnife.bind(this);
        this.recyclerView = this;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
//        inflate(context, R.layout.item_show_quiz, this);
        itemAdapter = new ItemAdapter(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(itemAdapter);
    }

    public void addItem(String question, String answer) {
        itemAdapter.addItem(new Item(question, answer));

    }

    public void addItem(String question, String answer, int colorQuestion, int colorAnswer) {
        itemAdapter.addItem(new Item(question, answer, colorQuestion, colorAnswer));

    }

    public void clear() {
        itemAdapter.clearItems();
    }

    public class Item {
        private String question;
        private String answer;
        private int colorQuestion;
        private int colorAnswer;

        public Item() {
        }

        public Item(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public Item(String question, String answer, int colorQuestion, int colorAnswer) {
            this.question = question;
            this.answer = answer;
            this.colorQuestion = colorQuestion;
            this.colorAnswer = colorAnswer;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public int getColorQuestion() {
            return colorQuestion;
        }

        public void setColorQuestion(int colorQuestion) {
            this.colorQuestion = colorQuestion;
        }

        public int getColorAnswer() {
            return colorAnswer;
        }

        public void setColorAnswer(int colorAnswer) {
            this.colorAnswer = colorAnswer;
        }
    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

        private List<Item> items;
        private Context context;

        public ItemAdapter(Context context) {
            this.context = context;
            items = new ArrayList<>();
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ItemHolder(View.inflate(context, R.layout.item_show_quiz, null));
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
            Item item = items.get(i);

            itemHolder.answer.setVisibility(VISIBLE);
            itemHolder.question.setText(item.getQuestion());
            itemHolder.question.setBackgroundColor(item.getColorQuestion());

            if (item.getAnswer() == null) {
                itemHolder.answer.setVisibility(GONE);
            } else {
                itemHolder.answer.setText(item.getAnswer());
                itemHolder.answer.setBackgroundColor(item.getColorQuestion());
            }

        }

        /**
         * Add item and notify you that a new item has been entered.
         *
         * @param item T
         */
        public void addItem(Item item) {
            if (item != null) {
                items.add(item);

                new Handler(Looper.getMainLooper()).post(() -> notifyItemInserted(items.size() - 1));
            }
        }

        /**
         * Clear the list of itemsList and notifies you that the data set has changed.
         */
        public void clearItems() {
            items.clear();

            new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.question)
            TextView question;

            @BindView(R.id.answer)
            TextView answer;

            public ItemHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
