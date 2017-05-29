package cn.coolbhu.sfexpress.service.serviceimpl;

import cn.coolbhu.sfexpress.dao.OrderMapper;
import cn.coolbhu.sfexpress.model.Order;
import cn.coolbhu.sfexpress.service.CartService;
import cn.coolbhu.sfexpress.service.Constant;
import cn.coolbhu.sfexpress.service.OrderService;
import cn.coolbhu.sfexpress.service.ProinfoService;
import cn.coolbhu.sfexpress.util.RandomUtils;
import cn.coolbhu.sfexpress.vo.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by brainy on 17-5-28.
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProinfoService proinfoService;

    @Autowired
    private CartService cartService;

    @Override
    public int addOrderByAddIdAndCartIds(String addid, String[] cartids, String userid) {

        //准备数据
        Order order = new Order();

        //初始化
        order.setOrderid(RandomUtils.randomId10());
        order.setOrdercreatetime(new Date());
        order.setPaymark(Constant.MARK_CODE_NOT_OK);
        order.setOrdermark(Constant.MARK_CODE_OK);

        //填充数据
        order.setAddid(addid);
        order.setUserid(userid);

        //计算总的价格
        List<CartInfo> cartInfoList = cartService.getCartInfoByCartIds(cartids);
        Double total = cartService.countTotalByCartInfos(cartInfoList);
        order.setTotalprice(total);

        //添加订单
        int result = orderMapper.insert(order);

        if (result > 0) {


            //添加订单商品
            for (CartInfo cartInfo : cartInfoList) {
                //添加尚品
                proinfoService.addProinfoByProId(order.getOrderid(), cartInfo.getProid());
            }
        }

        return result;
    }
}