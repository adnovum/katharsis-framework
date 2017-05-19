package io.katharsis.core.engine.error;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.katharsis.core.engine.document.ErrorData;
import io.katharsis.core.engine.document.ErrorDataBuilder;

public class ErrorDataMother {

    public static final String DETAIL = "detail";
    public static final String CODE = "code";
    public static final String ABOUT_LINK = "href";
    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String TITLE = "title";
    public static final String POINTER = "pointer";
    public static final String PARAMETER = "parameter";
    public static final Map<String, Object> META = new HashMap<>();

    public static final String META_KEY = "key";

    public static final String META_VALUE = "value";

    static {
        META.put(META_KEY, META_VALUE);
    }

    public static ErrorDataBuilder fullyPopulatedErrorDataBuilder() {
        return ErrorData.builder()
                .setDetail(DETAIL)
                .setStatus(STATUS)
                .setId(ID)
                .setCode(CODE)
                .setAboutLink(ABOUT_LINK)
                .setTitle(TITLE)
                .setSourcePointer(POINTER)
                .setSourceParameter(PARAMETER)
                .setMeta(META);
    }

    public static ErrorData fullyPopulatedErrorData() {
        return fullyPopulatedErrorDataBuilder().build();
    }

    public static List<ErrorData> oneSizeCollectionOfErrorData() {
        return Collections.singletonList(fullyPopulatedErrorData());
    }
}