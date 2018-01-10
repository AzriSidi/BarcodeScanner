package app.num.barcodescannerproject;

public class Contact {

    //private variables
    int _id;
    String _result;
    String _type;

    // constructor
    public Contact(String result,String type){
        this._result = result;
        this._type = type;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getResult(){
        return this._result;
    }

    // setting name
    public void setResult(String result){
        this._result = result;
    }

    public String getType() {
        return this._type;
    }

    public void setType(String type) {
        this._type = type;
    }
}
