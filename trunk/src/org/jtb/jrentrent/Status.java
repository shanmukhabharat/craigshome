package org.jtb.jrentrent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

public class Status {

    public ErrorType getErrorType() {
        return errorType;
    }
    public static enum ErrorType {
        TOO_MANY_RESULTS,
        NO_RESULTS,
        UNKNOWN;
    }

    private static Pattern TOO_MANY_RESULTS_PATTERN = Pattern.compile("Too many properties");
    private static Pattern NO_RESULTS_PATTERN = Pattern.compile("No properties");

    private boolean success;

    private String message;
    private ErrorType errorType = ErrorType.UNKNOWN;

    public Status(JSONObject jo) throws JSONException {
        if (jo.getString("Status") == null || !jo.getString("Status").equals("Success")) {
            success = false;
            message = jo.getString("Message");
            setErrorType();
        } else {
            success = true;
        }
    }

    private void setErrorType() {
        Matcher m = TOO_MANY_RESULTS_PATTERN.matcher(message);
        if (m.find()) {
            errorType = ErrorType.TOO_MANY_RESULTS;
            return;
        }
        m = NO_RESULTS_PATTERN.matcher(message);
        if (m.find()) {
            errorType = ErrorType.NO_RESULTS;
            return;
        }
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
