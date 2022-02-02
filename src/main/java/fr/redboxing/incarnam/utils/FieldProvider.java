package fr.redboxing.incarnam.utils;

public interface FieldProvider
{
    public static final String[] NO_FIELDS = new String[0];

    String[] getFields();

    Object getFieldValue(String p0);

    void setFieldValue(String p0, Object p1);

    void prependFieldValue(String p0, Object p1);

    void appendFieldValue(String p0, Object p1);

    boolean isFieldSynchronisable(String p0);
}
