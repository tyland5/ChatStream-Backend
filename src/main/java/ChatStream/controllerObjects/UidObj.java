package ChatStream.controllerObjects;

public class UidObj {
    private String uid;

    // For deserialisation purposes must have a zero-arg constructor.
    // https://stackoverflow.com/questions/48448079/json-parse-error-can-not-construct-instance-of-io-starter-topic-topic
    public UidObj(){}

    public UidObj(String uid){
        this.uid = uid;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
