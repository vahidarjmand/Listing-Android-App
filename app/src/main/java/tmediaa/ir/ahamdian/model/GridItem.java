package tmediaa.ir.ahamdian.model;

/**
 * Created by tmediaa on 11/28/2017.
 */

public class GridItem {
    private int _index;
    private int _id;
    private String _name;
    private int _path;
    private int _color;

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public int get_path() {
        return _path;
    }

    public void set_path(int _path) {
        this._path = _path;
    }

    public int get_color() {
        return _color;
    }

    public void set_color(int _color) {
        this._color = _color;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_index() {
        return _index;
    }

    public void set_index(int _index) {
        this._index = _index;
    }
}
