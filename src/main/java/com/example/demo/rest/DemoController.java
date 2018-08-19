package com.example.demo.rest;




import java.io.File;

import javax.validation.constraints.Null;

import com.example.demo.service.UploadService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private UploadService uploadService;

    @RequestMapping(value="/upload",method = RequestMethod.GET)
    public Boolean uploadFile() {
        return uploadService.uploadFile(new File("C:\\Users\\<User>\\Desktop\\filename.extn"));
    }

}
