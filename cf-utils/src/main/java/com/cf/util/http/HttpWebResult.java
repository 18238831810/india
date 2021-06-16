package com.cf.util.http;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/2/28
 */
@Slf4j
public class HttpWebResult {
    public static ResultJson getMonoResult(int code, String msg, Object data)
    {
        return new ResultJson( code, msg, data);
    }


    public static ResultJson getMonoSucResult(String msg, Object data) {
        return getMonoResult(0, msg, data);
    }

    public static ResultJson getMonoSucResult(Object data) {
        return getMonoResult(0, "success", data);
    }

    public static ResultJson getMonoError(int code,String errorMsg)
    {
        return getMonoResult(code, errorMsg, null);
    }

    public static ResultJson getMonoError(String errorMsg)
    {
        return getMonoResult(500, errorMsg, null);
    }
    public static ResultJson getMonoSucStr()
    {
        return getMonoResult(0, "success", null);
    }

    /****
     * 输出字符串
     *
     * @param msg
     */
    public static void print(HttpServletResponse response, String msg)
    {
        PrintWriter out = null;
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        msg = StringUtils.isBlank(msg) ? "" : msg;
        try
        {
            out = response.getWriter();
            out.println(msg);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            if (out != null)
            {
                out.flush();
                out.close();
            }
        }
    }

}
