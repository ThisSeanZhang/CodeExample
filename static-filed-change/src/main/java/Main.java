import java.lang.reflect.Field;

public class Main {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        Class<Config.Plugin> config = (Class<Config.Plugin>) Class.forName("Config$Plugin");
        Field peer_max_length = config.getField("PEER_MAX_LENGTH");
        System.out.println(peer_max_length.get(config).toString());

//        printInnerParamValue(Config.class);
        Config.Plugin.PEER_MAX_LENGTH = 100;
//        printInnerParamValue(Config.class);
        System.out.println(peer_max_length.get(config).toString());
    }

    public static void printInnerParamValue(Class<?> clasz){
        Class innerClazz[] = clasz.getDeclaredClasses();
        for(Class claszInner : innerClazz){
            Field[] fields = claszInner.getDeclaredFields();
            for(Field field : fields){
                try {
                    Object object = field.get(claszInner);
                    System.out.println("获取到的feild, name=" + field.getName()+",   value="+ object.toString());
                    //打印内容
                    /*
                    * 获取到的feild, name=version,   value=iphone6s[是手机不是吃的苹果]
                      获取到的feild, name=date,   value=生产日期 2017-06-13
                    * */
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
