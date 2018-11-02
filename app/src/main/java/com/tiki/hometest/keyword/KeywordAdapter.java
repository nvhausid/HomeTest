package com.tiki.hometest.keyword;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tiki.hometest.R;

import java.util.ArrayList;
import java.util.List;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder> {

    public static class KeywordViewHolder extends RecyclerView.ViewHolder {
        TextView mText;

        public KeywordViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView;
        }
    }

    private List<String> mKeywordList;
    private List<String> mKeywordFormattedList = new ArrayList<>();
    private KeywordClickListener mItemClickListener;
    private int[] mItemColors;

    /**
     * @param keywordList       Keywords to show
     * @param itemColors        Keyword background colors
     * @param itemClickListener Keyword click listener
     */
    public KeywordAdapter(List<String> keywordList, int[] itemColors,
                          KeywordClickListener itemClickListener) {
        mKeywordList = keywordList;
        mItemClickListener = itemClickListener;
        mItemColors = itemColors;

        // Format keywords for displaying
        for (String keyword : keywordList) {
            mKeywordFormattedList.add(formatKeyword(keyword));
        }
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyword_item, parent,
                false);
        return new KeywordViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordViewHolder holder, final int position) {
        holder.mText.setText(mKeywordFormattedList.get(position));

        // Set item background color
        Drawable background = holder.mText.getBackground();
        int colorIndex = position % mItemColors.length;// sequencing repeat the colors set
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(mItemColors[colorIndex]);
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(mItemColors[colorIndex]);
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor(mItemColors[colorIndex]);
        }

        holder.mText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.OnKeywordClick(mKeywordList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mKeywordList.size();
    }

    /**
     * Break keyword into 2 lines if it has more than 1 word, also trim spaces within
     * keyword if any.
     *
     * @param keyword Keyword to format
     * @return Formatted keyword
     */
    private String formatKeyword(String keyword) {
        String[] words = keyword.trim().split(" +");
        String result = new String();

        if (words.length < 2) {
            result = keyword.trim();
        } else if (words.length == 2) {
            // Only 2 words, just break them
            result = words[0] + "\n" + words[1];
        } else {
            // More than 2 words, make 2 lines have the most equally length as possible.
            int breakIndex;// second line word start index
            int length0, length1;// first/second line length

            breakIndex = length0 = length1 = 0;
            for (int i = 0; i < words.length; i++) {
                length1 += words[i].length();
            }

            // Find breakIndex
            while (length0 < length1) {
                if (length1 - words[breakIndex].length() > length0) {
                    length0 += words[breakIndex].length();
                    length1 -= words[breakIndex].length();
                    breakIndex++;
                } else {
                    break;
                }
            }

            for (int i = 0; i < words.length; i++) {
                if (i == breakIndex) {
                    result = result.trim() + "\n";
                }

                result += words[i];
                result += " ";
            }
            result.trim();
        }

        return result;
    }
}
