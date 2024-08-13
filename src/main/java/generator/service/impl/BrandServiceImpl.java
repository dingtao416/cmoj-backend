package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Brand;
import generator.service.BrandService;
import generator.mapper.BrandMapper;
import org.springframework.stereotype.Service;

/**
* @author 86166
* @description 针对表【brand】的数据库操作Service实现
* @createDate 2024-04-22 10:29:19
*/
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand>
    implements BrandService{

}




