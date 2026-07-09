```text
pom.xml
application.properties

travel-reimbursement/
├── reimbursement-back/
│   └── src/main/
│       └── java/com/example/reimbursementback/
│           ├── entity/business/   
│           │   ├── FkReimCostAllocation.java
│           │   ├── FkReimMain.java
│           │   ├── FkReimSubsidy.java
│           │   ├── FkReimSubsidyCalendar.java
│           │   └── FkReimTrip.java
│           ├── dto/
│           │   ├── ReimbursementQueryDTO.java  
│           │   └── ReimbursementSaveDTO.java   
│           ├── vo/
│           │   ├── ReimbursementDetailVO.java   
│           │   └── ReimbursementListVO.java    
│           ├── mapper/
│           └── service/
│           │   ├── FkReimCostAllocationService.java
│           │   ├── FkReimMainService.java
│           │   ├── FkReimSubsidyCalendarService.java
│           │   ├── FkReimSubsidyService.java
│           │   ├── FkReimTripService.java
│           ├── config/
│           │   └── MyBatisPlusConfig.java    
│           ├── mapper/   
│           │   ├── FkReimCostAllocationMapper.java
│           │   ├── FkReimMainMapper.java
│           │   ├── FkReimSubsidyCalendarMapper.java
│           │   ├── FkReimSubsidyMapper.java
│           │   └── FkReimTripMapper.java
│           ├── controller/
│           │   └── ReimbursementController.java
│           └── service/
│               └── impl/
│                     ├── FkReimMainServiceImpl.java
│
│
└── reimbursement-front/
    └── src/
        ├── views/
        │   └── ReimbursementDetail.vue
        └── components/
            ├── DataTable.vue
            └── detail/
                └── CostAllocationSection.vue
```
