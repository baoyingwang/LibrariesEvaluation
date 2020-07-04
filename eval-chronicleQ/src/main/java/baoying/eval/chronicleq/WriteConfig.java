package baoying.eval.chronicleq;

public enum WriteConfig {
    TEXT("/tmp/chronicleQ.sample.text"),
    SELF_DESCRIBE("/tmp/chronicleQ.sample.self_describe_text"),
    BYTES_IN_SELF_DESCRIBE("/tmp/chronicleQ.sample.bytes_in_self_describe");
    String file;
    WriteConfig(String path){
        this.file = path;
    }
}
