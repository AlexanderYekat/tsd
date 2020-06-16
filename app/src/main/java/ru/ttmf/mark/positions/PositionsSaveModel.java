package ru.ttmf.mark.positions;

public class PositionsSaveModel {
    public String Sgtin;
    public String FullSgtin;
    public Integer Count;

    public PositionsSaveModel(String Item, String fullItem, Integer count){
        this.Sgtin = Item;
        this.FullSgtin = fullItem;
        this.Count = count;
    }
}