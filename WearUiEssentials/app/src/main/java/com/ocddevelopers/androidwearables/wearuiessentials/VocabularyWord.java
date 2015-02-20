package com.ocddevelopers.androidwearables.wearuiessentials;

public class VocabularyWord {
    private String mWord, mExampleSentence, mCategory, mDefinition;
    private String mSynonyms;

    public VocabularyWord(String word, String category, String definition, String exampleSentence,
                          String synonyms) {
        mWord = word;
        mCategory = category;
        mDefinition = definition;
        mExampleSentence = exampleSentence;
        mSynonyms = synonyms;
    }

    public String getWord() {
        return mWord;
    }

    public void setWord(String mWord) {
        this.mWord = mWord;
    }

    public String getExampleSentence() {
        return mExampleSentence;
    }

    public void setExampleSentence(String exampleSentence) {
        mExampleSentence = exampleSentence;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getDefinition() {
        return mDefinition;
    }

    public void setDefinition(String definition) {
        mDefinition = definition;
    }

    public String getSynonyms() {
        return mSynonyms;
    }

    public void setSynonyms(String synonyms) {
        mSynonyms = synonyms;
    }
}
