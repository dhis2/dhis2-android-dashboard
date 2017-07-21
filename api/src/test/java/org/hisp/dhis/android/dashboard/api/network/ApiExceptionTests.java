package org.hisp.dhis.android.dashboard.api.network;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;

public class ApiExceptionTests {

    Response response;
    Converter converter;
    java.lang.reflect.Type type;

    @Test
    public void retrofit_network_exception_map_to_api_exception() {
        APIException apiException = getNetworkExceptionFromRetrofit();
        assertTrue(apiException.getKind().equals(APIException.Kind.NETWORK));
        assertTrue(apiException.getUrl().equals("test_message"));
    }

    @Test
    public void retrofit_conversion_exception_map_to_api_exception() {
        APIException apiException = getConversionExceptionFromRetrofit();
        assertTrue(apiException.getKind().equals(APIException.Kind.CONVERSION));
        assertTrue(apiException.getUrl().equals("test_message"));
    }

    @Test
    public void retrofit_unexpected_exception_map_to_api_exception() {
        APIException apiException = getUnexpectedExceptionFromRetrofit();
        assertTrue(apiException.getKind().equals(APIException.Kind.UNEXPECTED));
        assertTrue(apiException.getUrl().equals("test_message"));
    }

    @Test
    public void retrofit_http_error_exception_map_to_api_exception() {
        APIException apiException = getHttpErrorExceptionFromRetrofit();
        assertTrue(apiException.getKind().equals(APIException.Kind.HTTP));
        assertTrue(apiException.getUrl().equals("test_message"));
    }


    private APIException getNetworkExceptionFromRetrofit() {
        return APIException.fromRetrofitError(
                RetrofitError.networkError("test_message", new IOException()));
    }

    private APIException getConversionExceptionFromRetrofit() {
        return APIException.fromRetrofitError(
                RetrofitError.conversionError("test_message", response, converter,
                        type, new ConversionException("test_message")));
    }

    private APIException getUnexpectedExceptionFromRetrofit() {
        return APIException.fromRetrofitError(
                RetrofitError.unexpectedError("test_message", new IOException()));
    }

    private APIException getHttpErrorExceptionFromRetrofit() {
        List<Header> headerList = new ArrayList<>();
        Response response = new Response("test_url", 404, "Not found", headerList, null);
        return APIException.fromRetrofitError(
                RetrofitError.httpError("test_message", response, converter, type));
    }
}