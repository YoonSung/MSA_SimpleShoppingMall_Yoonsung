package mapper;

/**
 * Created by yoon on 15. 9. 2..
 */
enum ConstraintType {
    Type_String(String.class, "{String}"),
    Type_Long(Long.class, "{Long}"),
    Type_Float(Float.class, "{Float}"),
    Type_Integer(Integer.class, "{Integer}"),
    Type_Double(Double.class, "{Double}");

    private final Class claz;
    private final String stringValue;

    ConstraintType(Class claz, String stringValue) {
        this.claz = claz;
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static ConstraintType create(String word) {
        for (ConstraintType type : ConstraintType.values()) {
            if (type.getStringValue().equals(word))
                return type;
        }

        throw new IllegalArgumentException("Constraint Type is miss match");
    }

    //TODO
    public boolean isValidRequest(String urlParameter) {
        //this.claz.isAssignableFrom(urlParameter.getClass());
        return false;
    }
}
