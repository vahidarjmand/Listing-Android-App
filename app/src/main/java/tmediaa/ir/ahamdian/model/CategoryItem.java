package tmediaa.ir.ahamdian.model;

import java.util.ArrayList;

/**
 * Created by tmediaa on 9/28/2017.
 */

public class CategoryItem {
    private int id;
    private String name;
    private Integer parent_id;
    private int depth;
    private ArrayList<CategoryItem> childs;

    public ArrayList<CategoryItem> getChilds() {
        return childs;
    }

    public void setChilds(ArrayList<CategoryItem> childs) {
        this.childs = childs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParent_id() {
        return parent_id;
    }


    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
