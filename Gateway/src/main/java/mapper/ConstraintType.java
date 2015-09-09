package mapper;

/**
 * Created by yoon on 15. 9. 2..
 */
enum ConstraintType {
    Type_String(String.class, "{String}", value -> {}),
    Type_Long(Long.class, "{Long}", value -> Long.parseLong(value)),
    Type_Float(Float.class, "{Float}", value -> Float.parseFloat(value)),
    Type_Integer(Integer.class, "{Integer}", value -> Integer.parseInt(value)),
    Type_Double(Double.class, "{Double}", value -> Double.parseDouble(value));

    private final Class claz;
    private final String stringValue;
    private final Convertable convertable;

    ConstraintType(Class claz, String stringValue, Convertable convertable) {
        this.claz = claz;
        this.stringValue = stringValue;
        this.convertable = convertable;
    }

    String getStringValue() {
        return stringValue;
    }

    static ConstraintType create(String word) {
        for (ConstraintType type : ConstraintType.values()) {
            if (type.getStringValue().equals(word))
                return type;
        }

        throw new IllegalArgumentException("Constraint Type is miss match");
    }

    boolean isValidRequest(String urlParameter) {
        try {
            this.convertable.casting(urlParameter);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
