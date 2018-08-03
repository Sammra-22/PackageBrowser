package se.dohi.packagebrowser.model;

/**
 * Created by Sam22 on 9/28/15.
 */
public enum Environment {
    DEVELOPMENT,
    PRODUCTION;

    public static String[] toArray() {
        return new String[]{String.format("\n%s\n", Environment.values()[0].toString()),
                String.format("\n%s\n", Environment.values()[1].toString())};
    }

    @Override
    public String toString() {
        switch (this) {
            case DEVELOPMENT:
                return "Development";
            case PRODUCTION:
                return "Production";
        }
        return super.toString();
    }
}
