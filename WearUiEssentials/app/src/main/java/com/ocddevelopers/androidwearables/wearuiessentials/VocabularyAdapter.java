package com.ocddevelopers.androidwearables.wearuiessentials;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.Gravity;

/**
 * Displays a vocabulary word per row. Every row contains three columns with the word's
 * definition, an example sentence, and a list of synonyms.
 */
public class VocabularyAdapter extends FragmentGridPagerAdapter {
    private static final int NUMBER_OF_COLUMNS = 3;
    private VocabularyList mVocabularyList;
    private Drawable mBackground;

    public VocabularyAdapter(Context context, FragmentManager fm, VocabularyList vocabularyList) {
        super(fm);
        mVocabularyList = vocabularyList;
        mBackground = context.getDrawable(R.drawable.example_bg);
    }

    @Override
    public Fragment getFragment(int row, int col) {
        VocabularyWord word = mVocabularyList.getVocabularyWord(row);
        String description = null;
        switch (col) {
            case 0:
                description = word.getDefinition();
                break;
            case 1:
                description = "\"" + word.getExampleSentence() + "\"";
                break;
            case 2:
                description = word.getSynonyms();
                break;
        }

        CardFragment cardFragment = CardFragment.create(word.getWord() +
                " (" + word.getCategory() + ")", description);
        cardFragment.setCardGravity(Gravity.BOTTOM);
        cardFragment.setExpansionEnabled(true);
        cardFragment.setExpansionDirection(CardFragment.EXPAND_DOWN);
        cardFragment.setExpansionFactor(2f);

        return cardFragment;
    }

    @Override
    public Drawable getBackgroundForPage(int row, int column) {
        int drawableId = 0;

        // uncomment to try using different background for different columns
            /*
            switch(column) {
                case 0:
                    drawableId = R.drawable.definition_bg;
                    break;
                case 1:
                    drawableId = R.drawable.example_bg;
                    break;
                case 2:
                    drawableId = R.drawable.synonyms_bg;
                    break;
            }
            */

        drawableId = R.drawable.example_bg;
        return mBackground;
    }

    @Override
    public int getRowCount() {
        return mVocabularyList.size();
    }

    @Override
    public int getColumnCount(int row) {
        return NUMBER_OF_COLUMNS;
    }

    // Uncomment to test out fixed-movement paging.
    /*
    @Override
    public int getCurrentColumnForRow(int row, int currentColumn) {
        return currentColumn;
    }
    */
}