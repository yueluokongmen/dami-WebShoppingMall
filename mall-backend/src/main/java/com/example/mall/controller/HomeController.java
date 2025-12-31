package com.example.mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.Result;
import com.example.mall.entity.Category;
import com.example.mall.entity.ProductFamily;
import com.example.mall.entity.Spu;
import com.example.mall.mapper.CategoryMapper;
import com.example.mall.mapper.ProductFamilyMapper;
import com.example.mall.mapper.SpuMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductFamilyMapper productFamilyMapper;
    @Autowired
    private SpuMapper spuMapper;

    //定义两个简单的内部类VO，专门给前端导航栏用
    @Data
    public static class NavCategoryVO {
        private Integer id;
        private String name;
        private List<NavFamilyVO> children; // 该分类下的系列
    }

    @Data
    public static class NavFamilyVO {
        private Integer id;
        private String name;
        private String img; // 系列图(取自SPU)
        private Long linkSpuId; // 点击跳转的商品ID
    }

    // 获取首页分类导航数据
    // URL: http://localhost:8081/home/nav
    @GetMapping("/nav")
    public Result<List<NavCategoryVO>> getNav() {
        List<NavCategoryVO> result = new ArrayList<>();

        //查出所有大类 (Category)
        List<Category> categories = categoryMapper.selectList(null);

        //遍历大类，组装数据
        for (Category cat : categories) {
            NavCategoryVO catVO = new NavCategoryVO();
            catVO.setId(cat.getCategoryId());
            catVO.setName(cat.getCategoryName());

            //查该大类下的系列 (ProductFamily)
            LambdaQueryWrapper<ProductFamily> familyWrapper = new LambdaQueryWrapper<>();
            familyWrapper.eq(ProductFamily::getCategoryId, cat.getCategoryId());
            List<ProductFamily> families = productFamilyMapper.selectList(familyWrapper);

            List<NavFamilyVO> children = new ArrayList<>();
            for (ProductFamily family : families) {
                NavFamilyVO familyVO = new NavFamilyVO();
                familyVO.setId(family.getProductFamilyId());
                familyVO.setName(family.getProductFamilyName());

                //去SPU表找一张图给这个系列用
                LambdaQueryWrapper<Spu> spuWrapper = new LambdaQueryWrapper<>();
                spuWrapper.eq(Spu::getProductFamilyId, family.getProductFamilyId());
                spuWrapper.last("LIMIT 1"); // 只取一个商品就行
                Spu spu = spuMapper.selectOne(spuWrapper);

                if (spu != null) {
                    if (spu.getImages() != null && !spu.getImages().isEmpty()) {
                        familyVO.setImg(spu.getImages().get(0)); // 取第一张图
                    }
                    familyVO.setLinkSpuId(spu.getProductId()); //记录ID方便前端点击跳转
                } else {
                    //如果该系列下还没商品，给个默认图占位
                    familyVO.setImg("https://images.unsplash.com/photo-1598327105666-5b89351aff23?w=50&h=50&fit=crop");
                }

                children.add(familyVO);
            }

            catVO.setChildren(children);
            result.add(catVO);
        }

        return Result.success(result);
    }
}