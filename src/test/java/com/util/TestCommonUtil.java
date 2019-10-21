package com.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.esserver.SearchApplication;
import com.esserver.common.utils.CommonUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestCommonUtil {

  //  @Test
    public void test(){
        if (CommonUtil.isValidDate("a")){
            System.out.println("该字符串是日期格式");
        }else {
            System.out.println("该字符串不是日期格式");
        }
    }

    @Test
    public  void dataTest(){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long ms = 1504170831000L;
        java.util.Date date = new Date(ms* 1000);
        String str = sdf.format(date);
        System.out.println(str);
    }
}
