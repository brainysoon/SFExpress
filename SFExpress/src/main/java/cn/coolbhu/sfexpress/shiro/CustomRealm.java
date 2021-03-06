package cn.coolbhu.sfexpress.shiro;

import cn.coolbhu.sfexpress.model.User;
import cn.coolbhu.sfexpress.service.AdminService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lcyanxi on 17-4-9.
 */
public class CustomRealm extends AuthorizingRealm implements Authorizer {

    @Autowired
    private AdminService adminService;

    /**
     * 权限分配
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        //获取当前登录的用户名,等价于(String)principals.fromRealm(this.getName()).iterator().next()
        String staffSId = (String) principals.getPrimaryPrincipal();

        //保存授权信息
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        //从数据库中获取当前登录用户的 角色列表 和 权限列表
//        authorizationInfo.addRoles(roleService.listRoleNameBySId(staffSId));
//        authorizationInfo.addStringPermissions(permsService.listPermsNameBySId(staffSId));

        return authorizationInfo;
    }

    /**
     * 登录认证
     *
     * @param authcToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {

        //实际上这个authcToken是从LoginController里面currentUser.login(token)传过来的
        String phone = (String) authcToken.getPrincipal();

        User paperUser = adminService.getUserByPhone(phone);

        //如果没有该用户
        if (paperUser == null) {

            throw new UnknownAccountException();
        }

        //判断该账号是否被激活
        if (paperUser.getSlead() <= 0) {

            throw new LockedAccountException();
        }

        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配
        return new SimpleAuthenticationInfo(
                paperUser.getPhone(),
                paperUser.getPassword(),
                ByteSource.Util.bytes(paperUser.getPhone()),
                getName()
        );
    }
}
