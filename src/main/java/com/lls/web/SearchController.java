package com.lls.web;

import com.lls.commons.SearchEnum;
import com.lls.entiry.CreditSearchResponse;
import com.lls.search.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import oracle.jdbc.proxy.annotation.Post;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("search")
@Api(description = "档案主体查询")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @ApiOperation(value = "信用主体查询", notes = "根据关键字查询信用主体")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyWords", value = "关键字", dataType = "String"),
            @ApiImplicitParam(name = "baseCredit", value = "统一社会信用代码", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示多少条", dataType = "Integer"),
            @ApiImplicitParam(name = "pageNo", value = "当前页数", dataType = "Integer"),
            @ApiImplicitParam(name = "areaId", value = "区域ID", dataType = "String"),
            @ApiImplicitParam(name = "areaType", value = "区域类型", dataType = "String"),
            @ApiImplicitParam(name = "tags", value = "信用标签", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "查询类型 0 全部 1 名称 2统一社会信用代码" +
                    "3 企业法人 4  联系地址  5 经营范围", dataType = "String")})
    @RequestMapping(value = "index", method = RequestMethod.POST)
    public ResponseEntity search( @RequestParam(name="keyWords",defaultValue = "") String keyWords
            ,@RequestParam(name="baseCredit", defaultValue = "L")String baseCredit
            ,@RequestParam(name = "pageSize",defaultValue ="10") Integer pageSize
            ,@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo
            ,@RequestParam(name = "areaId", defaultValue = "" )String areaId
            ,@RequestParam(name="areaType", defaultValue = "")String areaType
            ,@RequestParam(name="tags",defaultValue = "") String tags
            ,@RequestParam(name="type",defaultValue = "0") String type){
        CreditSearchResponse creditSearchResponse= searchService.findListByKeyWords(keyWords,baseCredit,pageSize, pageNo, areaId,areaType,tags , type);
        return new ResponseEntity(creditSearchResponse,HttpStatus.OK);
    }







}
