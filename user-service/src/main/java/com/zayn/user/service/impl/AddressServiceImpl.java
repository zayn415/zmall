package com.zayn.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zayn.user.domain.po.Address;
import com.zayn.user.mapper.AddressMapper;
import com.zayn.user.service.IAddressService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {
}
