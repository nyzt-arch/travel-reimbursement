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

    @Autowired
    private FkReimTripService tripService;

    @Autowired
    private FkReimSubsidyService subsidyService;

    @Autowired
    private FkReimSubsidyCalendarService calendarService;

    @Autowired
    private FkReimCostAllocationService costAllocationService;

    @Autowired
    private BaseEmployeeMapper baseEmployeeMapper;

    @Autowired
    private BaseDepartmentMapper baseDepartmentMapper;

    @Autowired
    private BaseCompanyMapper baseCompanyMapper;

    @Autowired
    private BaseBusinessTypeMapper baseBusinessTypeMapper;

    @Autowired
    private BaseCityMapper baseCityMapper;

    @Autowired
    private BaseProjectMapper baseProjectMapper;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //分页查询+多条件筛选
    @Override
    public Page<ReimbursementListVO> queryList(ReimbursementQueryDTO queryDTO) {
        Page<FkReimMain> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<FkReimMain> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getReimNo())) {
            wrapper.like(FkReimMain::getReimNo, queryDTO.getReimNo());
        }
        if (StringUtils.hasText(queryDTO.getTitle())) {
            wrapper.like(FkReimMain::getReimbursementTitle, queryDTO.getTitle());
        }
        if (StringUtils.hasText(queryDTO.getReason())) {
            wrapper.like(FkReimMain::getBusinessTripReason, queryDTO.getReason());
        }
        if (StringUtils.hasText(queryDTO.getCompanyId())) {
            wrapper.eq(FkReimMain::getReimCompanyId, queryDTO.getCompanyId());
        }
        if (StringUtils.hasText(queryDTO.getDeptId())) {
            wrapper.eq(FkReimMain::getReimDepartmentId, queryDTO.getDeptId());
        }
        if (StringUtils.hasText(queryDTO.getReimburserId())) {
            wrapper.eq(FkReimMain::getReimburserId, queryDTO.getReimburserId());
        }
        if (StringUtils.hasText(queryDTO.getBusinessTypeId())) {
            wrapper.eq(FkReimMain::getBusinessTypeId, queryDTO.getBusinessTypeId());
        }

        // 默认按创建时间倒序
        wrapper.orderByDesc(FkReimMain::getCreationTime);

        Page<FkReimMain> entityPage = this.page(page, wrapper);
        Page<ReimbursementListVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());

        List<ReimbursementListVO> voList = entityPage.getRecords().stream().map(entity -> {
            ReimbursementListVO vo = new ReimbursementListVO();
            vo.setId(entity.getId());
            vo.setReimNo(entity.getReimNo());
            try {
                vo.setStatus(Integer.valueOf(entity.getStatus()));
            } catch (NumberFormatException e) {
                vo.setStatus(0);
            }
            vo.setReimburserId(entity.getReimburserId());
            vo.setReimburserNo(entity.getReimburserNo());
            vo.setReimburserName(entity.getReimburserName());
            vo.setReimDepartmentId(entity.getReimDepartmentId());
            vo.setReimDepartmentNo(entity.getReimDepartmentNo());
            vo.setReimDepartmentName(entity.getReimDepartmentName());
            vo.setReimCompanyId(entity.getReimCompanyId());
            vo.setReimCompanyNo(entity.getReimCompanyNo());
            vo.setReimCompanyName(entity.getReimCompanyName());
            vo.setBusinessTypeId(entity.getBusinessTypeId());
            vo.setBusinessTypeNo(entity.getBusinessTypeNo());
            vo.setBusinessTypeName(entity.getBusinessTypeName());
            vo.setTitle(entity.getReimbursementTitle());
            vo.setReason(entity.getBusinessTripReason());
            vo.setSubsidyTotal(entity.getSubsidyTotal());
            vo.setCreateTime(entity.getCreationTime() != null ? entity.getCreationTime().format(DATETIME_FORMATTER) : null);
            vo.setUpdateTime(entity.getUpdateTime() != null ? entity.getUpdateTime().format(DATETIME_FORMATTER) : null);
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    //点击单号/标题 查看报销单详情
    @Override
    public ReimbursementDetailVO getDetail(String id) {
        FkReimMain entity = this.getById(id);
        if (entity == null) {
            return null;
        }

        ReimbursementDetailVO vo = new ReimbursementDetailVO();
        vo.setId(entity.getId());
        vo.setReimNo(entity.getReimNo());
        vo.setTitle(entity.getReimbursementTitle());
        vo.setReason(entity.getBusinessTripReason());
        vo.setReimburserId(entity.getReimburserId());
        vo.setReimDepartmentId(entity.getReimDepartmentId());
        vo.setReimCompanyId(entity.getReimCompanyId());
        vo.setBusinessTypeId(entity.getBusinessTypeId());
        vo.setSubsidyTotal(entity.getSubsidyTotal());
        vo.setMealSubsidyTotal(entity.getMealAllowance());
        vo.setTransportSubsidyTotal(entity.getTransportationAllowance());
        vo.setCommSubsidyTotal(entity.getPhoneAllowance());
        vo.setRemark(entity.getRemarks());
        try {
            vo.setStatus(Integer.valueOf(entity.getStatus()));
        } catch (NumberFormatException e) {
            vo.setStatus(0);
        }
        vo.setCreateTime(entity.getCreationTime() != null ? entity.getCreationTime().format(DATETIME_FORMATTER) : null);
        vo.setUpdateTime(entity.getUpdateTime() != null ? entity.getUpdateTime().format(DATETIME_FORMATTER) : null);

        // 1. 查询行程列表
        List<FkReimTrip> trips = tripService.getTripsByReimId(id);
        Map<String, FkReimTrip> tripMap = trips.stream().collect(Collectors.toMap(FkReimTrip::getId, Function.identity(), (k1, k2) -> k1));
        vo.setTrips(trips.stream().map(t -> {
            ReimbursementDetailVO.TripVO tripVO = new ReimbursementDetailVO.TripVO();
            tripVO.setId(t.getId());
            tripVO.setReimId(t.getReimId());
            tripVO.setTravelerId(t.getPersonId());
            tripVO.setDepartCityNo(t.getDepartureCityNo());
            tripVO.setArriveCityNo(t.getArrivalCityNo());
            tripVO.setDepartDate(t.getStartDate() != null ? t.getStartDate().format(DATE_FORMATTER) : null);
            tripVO.setArriveDate(t.getEndDate() != null ? t.getEndDate().format(DATE_FORMATTER) : null);
            tripVO.setTripDesc(t.getTripDesc());
            return tripVO;
        }).collect(Collectors.toList()));

        // 2. 查询补助信息列表并关联其日历明细
        List<FkReimSubsidy> subsidies = subsidyService.getSubsidiesByReimId(id);
        vo.setSubsidies(subsidies.stream().map(s -> {
            ReimbursementDetailVO.SubsidyVO subVO = new ReimbursementDetailVO.SubsidyVO();
            subVO.setId(s.getId());
            subVO.setReimId(s.getReimId());
            subVO.setTripId(s.getTripId());
            subVO.setTravelerId(s.getPersonId());
            subVO.setStartDate(s.getStartDate() != null ? s.getStartDate().format(DATE_FORMATTER) : null);
            subVO.setEndDate(s.getEndDate() != null ? s.getEndDate().format(DATE_FORMATTER) : null);
            subVO.setSubsidyDays(s.getDays());

            // 用 tripId 匹配行程的 arrivalCityNo，作为前端所需的 subsidyCityNo
            FkReimTrip trip = tripMap.get(s.getTripId());
            subVO.setSubsidyCityNo(trip != null ? trip.getArrivalCityNo() : null);

            subVO.setApplyAmount(s.getApplyAmount());
            subVO.setSubsidyAmount(s.getSubsidyAmount());

            // 查询该条补助对应的日历明细
            List<FkReimSubsidyCalendar> calendars = calendarService.getCalendarsBySubsidyId(s.getId());
            subVO.setCalendars(calendars.stream().map(c -> {
                ReimbursementDetailVO.CalendarVO calVO = new ReimbursementDetailVO.CalendarVO();
                calVO.setId(c.getId());
                calVO.setSubsidyInfoId(c.getSubsidyId());
                calVO.setSubsidyDate(c.getCalendarDate() != null ? c.getCalendarDate().format(DATE_FORMATTER) : null);
                calVO.setWeekDay(c.getWeekDay());

                // 将数据库冗余的 cityName (实际存 cityNo) 传递给前端的 cityNo 字段
                calVO.setCityNo(c.getCityName());

                try { calVO.setMealChecked(Integer.valueOf(c.getMealChecked())); } catch (Exception e) { calVO.setMealChecked(0); }
                calVO.setMealStandard(c.getMealStandard());
                calVO.setMealAmount(c.getMealAmount());

                try { calVO.setTransportChecked(Integer.valueOf(c.getTransportChecked())); } catch (Exception e) { calVO.setTransportChecked(0); }
                calVO.setTransportStandard(c.getTransportStandard());
                calVO.setTransportAmount(c.getTransportAmount());

                try { calVO.setCommChecked(Integer.valueOf(c.getPhoneChecked())); } catch (Exception e) { calVO.setCommChecked(0); }
                calVO.setCommStandard(c.getPhoneStandard());
                calVO.setCommAmount(c.getPhoneAmount());
                return calVO;
            }).collect(Collectors.toList()));

            return subVO;
        }).collect(Collectors.toList()));

        // 3. 查询费用分摊列表
        List<FkReimCostAllocation> allocations = costAllocationService.getAllocationsByReimId(id);
        vo.setAllocations(allocations.stream().map(a -> {
            ReimbursementDetailVO.CostAllocationVO allocVO = new ReimbursementDetailVO.CostAllocationVO();
            allocVO.setId(a.getId());
            allocVO.setReimId(a.getReimId());
            allocVO.setCompanyId(a.getCompanyId());
            allocVO.setProjectId(a.getProjectId());
            allocVO.setAllocRatio(a.getRatio());
            allocVO.setAllocAmount(a.getAmount());
            allocVO.setSortOrder(a.getSortOrder());
            return allocVO;
        }).collect(Collectors.toList()));

        return vo;
    }

    //保存草稿
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveDraft(ReimbursementSaveDTO saveDTO) {
        return saveOrUpdateMain(saveDTO, "0");
    }

    //提交报销单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReimbursement(ReimbursementSaveDTO saveDTO) {
        // 核心表单属性必填校验
        if (!StringUtils.hasText(saveDTO.getTitle())) {
            throw new RuntimeException("报销单标题不能为空！");
        }
        if (!StringUtils.hasText(saveDTO.getReimburserId())) {
            throw new RuntimeException("报销人不能为空！");
        }
        if (!StringUtils.hasText(saveDTO.getReimDepartmentId())) {
            throw new RuntimeException("报销部门不能为空！");
        }
        if (!StringUtils.hasText(saveDTO.getReimCompanyId())) {
            throw new RuntimeException("归属公司不能为空！");
        }
        if (!StringUtils.hasText(saveDTO.getBusinessTypeId())) {
            throw new RuntimeException("业务类型不能为空！");
        }

        // 行程校验
        if (saveDTO.getTrips() == null || saveDTO.getTrips().isEmpty()) {
            throw new RuntimeException("行程明细不能为空！");
        }

        // 分摊比例与金额校验
        if (saveDTO.getAllocations() == null || saveDTO.getAllocations().isEmpty()) {
            throw new RuntimeException("费用分摊明细不能为空！");
        }

        BigDecimal ratioSum = BigDecimal.ZERO;
        BigDecimal amountSum = BigDecimal.ZERO;
        for (ReimbursementSaveDTO.CostAllocationDTO alloc : saveDTO.getAllocations()) {
            ratioSum = ratioSum.add(alloc.getAllocRatio() != null ? alloc.getAllocRatio() : BigDecimal.ZERO);
            amountSum = amountSum.add(alloc.getAllocAmount() != null ? alloc.getAllocAmount() : BigDecimal.ZERO);
        }

        if (ratioSum.compareTo(BigDecimal.ONE) != 0) {
            throw new RuntimeException("费用分摊比例之和必须等于100%！当前合计为：" + ratioSum.multiply(new BigDecimal("100")) + "%");
        }

        BigDecimal subsidyTotal = saveDTO.getSubsidyTotal() != null ? saveDTO.getSubsidyTotal() : BigDecimal.ZERO;
        if (amountSum.compareTo(subsidyTotal) != 0) {
            throw new RuntimeException("费用分摊金额之和(" + amountSum + ")必须等于补助总金额(" + subsidyTotal + ")！");
        }

        saveOrUpdateMain(saveDTO, "1");
    }

    //删除报销单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReimbursement(String id) {
        // 级联清空关联子表
        tripService.deleteTripsByReimId(id);
        subsidyService.deleteSubsidiesByReimId(id); // 内部会同步清理 calendar
        costAllocationService.deleteAllocationsByReimId(id);
        // 删除主单
        this.removeById(id);
    }

    //报废报销单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReimbursement(String id) {
        FkReimMain main = this.getById(id);
        if (main != null) {
            main.setStatus("2"); // 已作废
            main.setUpdateTime(LocalDateTime.now());
            this.updateById(main);
        }
    }

    //自动生成报销单号（私有方法）
    private String saveOrUpdateMain(ReimbursementSaveDTO saveDTO, String status) {
        boolean isNew = false;
        FkReimMain main = new FkReimMain();

        // 识别是新增，还是修改时主单已不存在（回退为新增流程）
        boolean shouldCreateNew = saveDTO.getId() == null || saveDTO.getId().trim().isEmpty()
                || saveDTO.getId().startsWith("NEW_")
                || saveDTO.getId().startsWith("COPY_")
                || saveDTO.getId().startsWith("R");

        if (!shouldCreateNew) {
            main.setId(saveDTO.getId());
            FkReimMain existing = this.getById(saveDTO.getId());
            if (existing != null) {
                main.setReimNo(existing.getReimNo());
                main.setCreationTime(existing.getCreationTime());
            } else {
                shouldCreateNew = true;
            }
        }

        // 统一处理主单据的初始化和单号生成逻辑，避免代码冗余
        if (shouldCreateNew) {
            isNew = true;
            main.setId(null);
            main.setCreationTime(LocalDateTime.now());
            // 自动生成报销单号
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String prefix = "RCBX" + dateStr;
            Long todayCount = this.count(new LambdaQueryWrapper<FkReimMain>().like(FkReimMain::getReimNo, prefix + "%"));
            String reimNo = prefix + String.format("%03d", todayCount + 1);
            main.setReimNo(reimNo);
        }

        // 主表冗余字段查库补录
        if (saveDTO.getReimburserId() != null) {
            BaseEmployee emp = baseEmployeeMapper.selectById(saveDTO.getReimburserId());
            if (emp != null) {
                main.setReimburserId(emp.getReimburserId());
                main.setReimburserNo(emp.getReimburserNo());
                main.setReimburserName(emp.getReimburserName());
            }
        }
        if (saveDTO.getReimDepartmentId() != null) {
            BaseDepartment dept = baseDepartmentMapper.selectById(saveDTO.getReimDepartmentId());
            if (dept != null) {
                main.setReimDepartmentId(dept.getReimDepartmentId());
                main.setReimDepartmentNo(dept.getReimDepartmentNo());
                main.setReimDepartmentName(dept.getReimDepartmentName());
            }
        }
        if (saveDTO.getReimCompanyId() != null) {
            BaseCompany comp = baseCompanyMapper.selectById(saveDTO.getReimCompanyId());
            if (comp != null) {
                main.setReimCompanyId(comp.getReimCompanyId());
                main.setReimCompanyNo(comp.getReimCompanyNo());
                main.setReimCompanyName(comp.getReimCompanyName());
            }
        }
        if (saveDTO.getBusinessTypeId() != null) {
            BaseBusinessType bt = baseBusinessTypeMapper.selectById(saveDTO.getBusinessTypeId());
            if (bt != null) {
                main.setBusinessTypeId(bt.getBusinessTypeId());
                main.setBusinessTypeNo(bt.getBusinessTypeNo());
                main.setBusinessTypeName(bt.getBusinessTypeName());
            }
        }

        main.setReimbursementTitle(saveDTO.getTitle());
        main.setBusinessTripReason(saveDTO.getReason());
        main.setSubsidyTotal(saveDTO.getSubsidyTotal());
        main.setMealAllowance(saveDTO.getMealSubsidyTotal());
        main.setTransportationAllowance(saveDTO.getTransportSubsidyTotal());
        main.setPhoneAllowance(saveDTO.getCommSubsidyTotal());
        main.setRemarks(saveDTO.getRemark());
        main.setStatus(status);
        main.setUpdateTime(LocalDateTime.now());

        // 统一填入创建人（冗余演示）
        main.setCreateUserId("system");
        main.setCreateUserName("系统管理员");

        // 1. 保存/更新主表
        this.saveOrUpdate(main);
        String reimId = main.getId();

        // 2. 如果是修改，清空历史从表
        if (!isNew) {
            tripService.deleteTripsByReimId(reimId);
            subsidyService.deleteSubsidiesByReimId(reimId); // 连同 calendar 一并清除
            costAllocationService.deleteAllocationsByReimId(reimId);
        }

        // 3. 批量保存最新的行程数据
        if (saveDTO.getTrips() != null && !saveDTO.getTrips().isEmpty()) {
            List<FkReimTrip> tripEntities = new ArrayList<>();
            int sort = 1;
            for (ReimbursementSaveDTO.TripDTO tripDTO : saveDTO.getTrips()) {
                FkReimTrip trip = new FkReimTrip();

                if (isNew || tripDTO.getId() == null || tripDTO.getId().trim().isEmpty()
                        || tripDTO.getId().startsWith("NEW_")
                        || tripDTO.getId().startsWith("COPY_")
                        || tripDTO.getId().startsWith("T_")) {
                    trip.setId(null);
                } else {
                    trip.setId(tripDTO.getId());
                }

                trip.setReimId(reimId);
                trip.setPersonId(tripDTO.getTravelerId());

                // 补全出行人员工号姓名
                if (tripDTO.getTravelerId() != null) {
                    BaseEmployee emp = baseEmployeeMapper.selectById(tripDTO.getTravelerId());
                    if (emp != null) {
                        trip.setPersonNo(emp.getReimburserNo());
                        trip.setPersonName(emp.getReimburserName());
                    }
                }

                trip.setDepartureCityNo(tripDTO.getDepartCityNo());
                if (tripDTO.getDepartCityNo() != null) {
                    BaseCity departureCity = baseCityMapper.selectById(tripDTO.getDepartCityNo());
                    if (departureCity != null) {
                        trip.setDepartureCityName(departureCity.getCityName());
                    }
                }

                trip.setArrivalCityNo(tripDTO.getArriveCityNo());
                if (tripDTO.getArriveCityNo() != null) {
                    BaseCity arrivalCity = baseCityMapper.selectById(tripDTO.getArriveCityNo());
                    if (arrivalCity != null) {
                        trip.setArrivalCityName(arrivalCity.getCityName());
                    }
                }

                trip.setStartDate(tripDTO.getDepartDate() != null ? LocalDate.parse(tripDTO.getDepartDate(), DATE_FORMATTER) : null);
                trip.setEndDate(tripDTO.getArriveDate() != null ? LocalDate.parse(tripDTO.getArriveDate(), DATE_FORMATTER) : null);
                trip.setTripDesc(tripDTO.getTripDesc());
                trip.setSortOrder(sort++);
                trip.setCreationTime(LocalDateTime.now());
                trip.setUpdateTime(LocalDateTime.now());

                tripEntities.add(trip);
            }
            tripService.saveBatch(tripEntities);

            // 修改前端传来的 tripId 占位符，匹配最新的数据库生成主键ID (为 subsidy 明细关联做准备)
            if (saveDTO.getSubsidies() != null) {
                for (int i = 0; i < saveDTO.getTrips().size(); i++) {
                    String frontTripId = saveDTO.getTrips().get(i).getId();
                    String dbTripId = tripEntities.get(i).getId();

                    for (ReimbursementSaveDTO.SubsidyDTO sub : saveDTO.getSubsidies()) {
                        if (sub.getTripId().equals(frontTripId)) {
                            sub.setTripId(dbTripId);
                        }
                    }
                }
            }
        }

        // 4. 保存补助信息明细 & 补助日历明细
        if (saveDTO.getSubsidies() != null && !saveDTO.getSubsidies().isEmpty()) {
            subsidyService.saveSubsidiesAndCalendars(reimId, saveDTO.getSubsidies(), isNew);
        }

        // 5. 保存费用分摊数据
        if (saveDTO.getAllocations() != null && !saveDTO.getAllocations().isEmpty()) {
            List<FkReimCostAllocation> allocEntities = new ArrayList<>();
            int sort = 1;
            for (ReimbursementSaveDTO.CostAllocationDTO allocDTO : saveDTO.getAllocations()) {
                FkReimCostAllocation allocation = new FkReimCostAllocation();

                if (isNew || allocDTO.getId() == null || allocDTO.getId().trim().isEmpty()
                        || allocDTO.getId().startsWith("NEW_")
                        || allocDTO.getId().startsWith("COPY_")
                        || allocDTO.getId().startsWith("A_")) {
                    allocation.setId(null);
                } else {
                    allocation.setId(allocDTO.getId());
                }

                allocation.setReimId(reimId);
                allocation.setCompanyId(allocDTO.getCompanyId());

                // 补全归属公司编号/姓名
                if (allocDTO.getCompanyId() != null) {
                    BaseCompany comp = baseCompanyMapper.selectById(allocDTO.getCompanyId());
                    if (comp != null) {
                        allocation.setCompanyNo(comp.getReimCompanyNo());
                        allocation.setCompanyName(comp.getReimCompanyName());
                    }
                }

                allocation.setProjectId(allocDTO.getProjectId());
                // 补全项目信息
                if (allocDTO.getProjectId() != null) {
                    BaseProject proj = baseProjectMapper.selectById(allocDTO.getProjectId());
                    if (proj != null) {
                        allocation.setProjectNo(proj.getProjectNo());
                        allocation.setProjectName(proj.getProjectName());
                    }
                }

                allocation.setRatio(allocDTO.getAllocRatio());
                allocation.setAmount(allocDTO.getAllocAmount());
                allocation.setSortOrder(sort++);
                allocation.setCreationTime(LocalDateTime.now());
                allocation.setUpdateTime(LocalDateTime.now());

                allocEntities.add(allocation);
            }
            costAllocationService.saveBatch(allocEntities);
        }

        return reimId;
    }
}