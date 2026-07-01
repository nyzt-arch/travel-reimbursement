package com.example.reimbursementback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reimbursementback.dto.ReimbursementQueryDTO;
import com.example.reimbursementback.dto.ReimbursementSaveDTO;
import com.example.reimbursementback.entity.base.*;
import com.example.reimbursementback.entity.business.FkReimCostAllocation;
import com.example.reimbursementback.entity.business.FkReimMain;
import com.example.reimbursementback.entity.business.FkReimSubsidy;
import com.example.reimbursementback.entity.business.FkReimTrip;
import com.example.reimbursementback.entity.business.FkReimSubsidyCalendar;
import com.example.reimbursementback.mapper.*;
import com.example.reimbursementback.service.*;
import com.example.reimbursementback.vo.ReimbursementDetailVO;
import com.example.reimbursementback.vo.ReimbursementListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FkReimMainServiceImpl extends ServiceImpl<FkReimMainMapper, FkReimMain> implements FkReimMainService {

}
