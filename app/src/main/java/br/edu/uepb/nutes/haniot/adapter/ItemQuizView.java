package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemQuizView extends RelativeLayout {

    ItemAdapter itemAdapter;
    GridView recyclerView;
    RelativeLayout rootView;

    public ItemQuizView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ButterKnife.bind(this);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        rootView = (RelativeLayout) inflate(context, R.layout.card_quiz_answers, this);
        recyclerView = findViewById(R.id.items);
        itemAdapter = new ItemAdapter(context);
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

    public class ItemAdapter extends BaseAdapter {

        private List<Item> items;
        private Context context;

        public ItemAdapter(Context context) {
            this.context = context;
            items = new ArrayList<>();
        }

        /**
         * Add item and notify you that a new item has been entered.
         *
         * @param item T
         */
        public void addItem(Item item) {
            if (item != null) {
                items.add(item);

                new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
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
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Item item = items.get(position);

            // 2
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView = layoutInflater.inflate(R.layout.item_show_quiz, null);
            }
            TextView question = convertView.findViewById(R.id.question);
            TextView answer = convertView.findViewById(R.id.answer);

            answer.setText(item.getAnswer());
            question.setText(item.getQuestion());

            return convertView;
        }
    }
}
