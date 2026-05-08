-- CodeInspire Database Initialization Script
-- Version: 1.0
-- Date: 2026-05-08

CREATE DATABASE IF NOT EXISTS codeinspire DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE codeinspire;

-- ============================================================
-- 用户表 (users)
-- ============================================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 用户画像表 (user_profiles)
-- ============================================================
CREATE TABLE user_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    -- 教育背景
    school_level VARCHAR(30) NOT NULL COMMENT '985/211/一本/二本/独立学院/民办本科/专科',
    school_type VARCHAR(30) COMMENT '综合类/理工类/师范类/专科等',
    education_level VARCHAR(20) NOT NULL COMMENT '专科/本科/硕士/博士',
    major VARCHAR(100) COMMENT '所读专业：计算机科学与技术/软件工程/信息安全等',
    -- 时间维度
    grade VARCHAR(20) COMMENT '大一/大二/大三/大四/研一/研二等',
    urgency_level VARCHAR(20) COMMENT '紧迫程度：紧急(6个月内)/一般(1年内)/充裕(2年+)',
    weekly_available_hours INT COMMENT '每周可用学习时间(小时)',
    coursework_pressure VARCHAR(20) COMMENT '课业压力：轻松/一般/繁重/考研准备中',
    -- 地理位置
    target_city_level VARCHAR(30) COMMENT '目标城市级别：一线/新一线/二三线',
    hometown_consideration VARCHAR(50) COMMENT '户籍/家乡考虑',
    industry_preference VARCHAR(50) COMMENT '产业聚集偏好：电商/游戏/金融/互联网等',
    -- 经济约束
    payment_willingness VARCHAR(30) COMMENT '付费意愿：完全免费/低价(<500)/可付费(500-2000)/不差钱',
    computer_config VARCHAR(30) COMMENT '电脑配置：普通笔记本/游戏本/台式机/Mac',
    self_learning_ability VARCHAR(20) COMMENT '自学能力：强/中/弱',
    economic_pressure VARCHAR(20) COMMENT '经济压力：无负担/有一定压力/需要尽快变现',
    -- 求职目标
    current_status VARCHAR(30) COMMENT '当前位置：大一在读/春招/秋招/二战/待业等',
    major_direction VARCHAR(50) COMMENT '专业方向：后端开发/前端开发/全栈/AI等',
    target_position VARCHAR(50) COMMENT '目标岗位：后端/全栈/嵌入式/运维/DevOps等',
    target_company VARCHAR(50) COMMENT '目标企业：大厂/中厂/小厂/外企/国企/创业公司/考公',
    expected_salary VARCHAR(30) COMMENT '期望薪资',
    -- 技术背景
    skills TEXT COMMENT '掌握的技能，JSON格式存储',
    projects TEXT COMMENT '项目经验，JSON格式存储',
    certifications TEXT COMMENT '认证证书，JSON格式存储',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户画像表';

-- ============================================================
-- 灵感记录表 (inspirations)
-- ============================================================
CREATE TABLE inspirations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200),
    content TEXT NOT NULL,
    category VARCHAR(50),
    tags JSON,
    is_favorite BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='灵感记录表';

-- ============================================================
-- 规划表 (plans)
-- ============================================================
CREATE TABLE plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'active' COMMENT 'active/completed/paused/archived',
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='规划表';

-- ============================================================
-- 任务表 (tasks)
-- ============================================================
CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'pending' COMMENT 'pending/in_progress/completed/paused/cancelled/overdue',
    priority INT DEFAULT 0,
    due_date DATE,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plan_id) REFERENCES plans(id),
    INDEX idx_plan_id (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- ============================================================
-- 对话记录表 (conversations)
-- ============================================================
CREATE TABLE conversations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_id VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL COMMENT 'consultation/interview/planning/general',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_session (user_id, session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话记录表';

-- ============================================================
-- 消息记录表 (messages)
-- ============================================================
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL COMMENT 'user/assistant/system',
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id),
    INDEX idx_conversation_id (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息记录表';

-- ============================================================
-- Prompt模板表 (prompts)
-- ============================================================
CREATE TABLE prompts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '模板名称',
    scene VARCHAR(50) NOT NULL COMMENT '应用场景:tech_stack/learning_path/resume/interview/job_search',
    content TEXT NOT NULL COMMENT 'Prompt模板内容',
    variables JSON COMMENT '模板变量定义',
    version INT DEFAULT 1 COMMENT '版本号',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态:active/draft/deprecated',
    is_ab_test BOOLEAN DEFAULT FALSE COMMENT '是否参与A/B测试',
    ab_group VARCHAR(10) COMMENT 'A/B测试分组:A/B',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    satisfaction_score DECIMAL(3,2) COMMENT '平均满意度评分',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_scene_status (scene, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Prompt模板表';

-- ============================================================
-- Prompt版本历史表 (prompt_versions)
-- ============================================================
CREATE TABLE prompt_versions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    prompt_id BIGINT NOT NULL,
    version INT NOT NULL COMMENT '版本号',
    content TEXT NOT NULL COMMENT '模板内容快照',
    change_log TEXT COMMENT '变更说明',
    created_by BIGINT COMMENT '创建人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (prompt_id) REFERENCES prompts(id),
    INDEX idx_prompt_id (prompt_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Prompt版本历史表';

-- ============================================================
-- 知识库索引表 (knowledge_base)
-- ============================================================
CREATE TABLE knowledge_base (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category VARCHAR(50) NOT NULL COMMENT '知识分类:interview/resources/salary/company',
    subcategory VARCHAR(100) COMMENT '子分类',
    title VARCHAR(200) NOT NULL COMMENT '知识标题',
    content TEXT NOT NULL COMMENT '知识内容',
    content_vector BLOB COMMENT '内容向量(用于RAG)',
    source_url VARCHAR(500) COMMENT '来源URL',
    source_name VARCHAR(100) COMMENT '来源名称',
    tags JSON COMMENT '标签',
    confidence_level VARCHAR(20) DEFAULT 'high' COMMENT '可信度:high/medium/low',
    data_collected_at TIMESTAMP COMMENT '数据采集时间',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态:active/pending/archived',
    usage_count INT DEFAULT 0 COMMENT '被引用次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库索引表';

-- ============================================================
-- 系统通知表 (notifications)
-- ============================================================
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL COMMENT '通知类型:task_reminder/time_node/progress_warning/ai_reply/system',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    data JSON COMMENT '关联数据(如任务ID、计划ID等)',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    read_at TIMESTAMP COMMENT '阅读时间',
    channel VARCHAR(20) DEFAULT 'in_app' COMMENT '通知渠道:in_app/email/websocket',
    send_status VARCHAR(20) DEFAULT 'pending' COMMENT '发送状态:pending/sent/failed',
    scheduled_at TIMESTAMP COMMENT '计划发送时间',
    sent_at TIMESTAMP COMMENT '实际发送时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_scheduled (scheduled_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统通知表';

-- ============================================================
-- 用户反馈表 (user_feedbacks)
-- ============================================================
CREATE TABLE user_feedbacks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    message_id BIGINT NOT NULL COMMENT '关联的消息ID',
    type VARCHAR(20) NOT NULL COMMENT '反馈类型:like/dislike/report',
    rating INT COMMENT '满意度评分1-5',
    comment TEXT COMMENT '详细反馈',
    feedback_details JSON COMMENT '反馈详情(如具体哪部分不满意)',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '处理状态:pending/reviewed/resolved',
    admin_reply TEXT COMMENT '管理员回复',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (message_id) REFERENCES messages(id),
    INDEX idx_message (message_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户反馈表';

-- ============================================================
-- AI调用日志表 (ai_call_logs)
-- ============================================================
CREATE TABLE ai_call_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    provider VARCHAR(30) NOT NULL COMMENT 'AI提供商:mimo/deepseek/zhipu/qwen',
    model VARCHAR(50) NOT NULL COMMENT '模型名称',
    prompt_template_id BIGINT COMMENT '使用的Prompt模板ID',
    prompt_version INT COMMENT 'Prompt版本',
    input_tokens INT COMMENT '输入Token数',
    output_tokens INT COMMENT '输出Token数',
    total_tokens INT COMMENT '总Token数',
    latency_ms INT COMMENT '响应延迟(毫秒)',
    cost DECIMAL(10,4) COMMENT '调用成本',
    status VARCHAR(20) NOT NULL COMMENT '调用状态:success/failed/timeout/rate_limited/cancelled',
    error_message TEXT COMMENT '错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_provider (provider),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI调用日志表';

-- ============================================================
-- 能力评估记录表 (skill_assessments)
-- ============================================================
CREATE TABLE skill_assessments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    skill_category VARCHAR(50) NOT NULL COMMENT '技能分类:java/python/database/algorithm等',
    skill_name VARCHAR(100) NOT NULL COMMENT '技能名称',
    level INT DEFAULT 0 COMMENT '能力等级 0-100',
    assessment_method VARCHAR(20) DEFAULT 'self_report' COMMENT '评估方式:self_report/test/ai_estimate',
    evidence JSON COMMENT '评估证据:测试正确率/项目经验等',
    assessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_skill (user_id, skill_category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='能力评估记录表';

-- ============================================================
-- A/B测试实验表 (ab_experiments)
-- ============================================================
CREATE TABLE ab_experiments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '实验名称',
    type VARCHAR(30) NOT NULL COMMENT '实验类型:prompt/model/strategy',
    description TEXT COMMENT '实验描述',
    control_prompt_id BIGINT COMMENT '对照组Prompt ID',
    treatment_prompt_id BIGINT COMMENT '实验组Prompt ID',
    traffic_ratio DECIMAL(5,2) DEFAULT 50.00 COMMENT '实验组流量比例(%)',
    min_sample_size INT DEFAULT 100 COMMENT '最小样本量',
    start_time TIMESTAMP COMMENT '开始时间',
    end_time TIMESTAMP COMMENT '结束时间',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态:draft/running/paused/completed',
    metrics JSON COMMENT '评估指标定义',
    result JSON COMMENT '实验结果分析',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='A/B测试实验表';

-- ============================================================
-- A/B测试流量分组表 (ab_user_groups)
-- ============================================================
CREATE TABLE ab_user_groups (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    experiment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    group_name VARCHAR(20) NOT NULL COMMENT '分组名称:control/treatment',
    user_hash VARCHAR(64) NOT NULL COMMENT '用户Hash(用于一致性分桶)',
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (experiment_id) REFERENCES ab_experiments(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_experiment_user (experiment_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='A/B测试流量分组表';

-- ============================================================
-- 操作日志表 (operation_logs)
-- ============================================================
CREATE TABLE operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    username VARCHAR(50) COMMENT '操作用户名',
    module VARCHAR(50) NOT NULL COMMENT '操作模块',
    operation VARCHAR(100) NOT NULL COMMENT '操作类型',
    method VARCHAR(50) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_status INT COMMENT '响应状态码',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    execution_time_ms INT COMMENT '执行时间(毫秒)',
    error_message TEXT COMMENT '错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_module (module),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================================
-- 审计日志表 (audit_logs)
-- ============================================================
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '操作用户ID',
    admin_id BIGINT COMMENT '管理员ID',
    action VARCHAR(50) NOT NULL COMMENT '操作类型:account_delete/data_export/permission_change等',
    target_type VARCHAR(50) COMMENT '操作对象类型',
    target_id BIGINT COMMENT '操作对象ID',
    old_value JSON COMMENT '变更前的值',
    new_value JSON COMMENT '变更后的值',
    reason TEXT COMMENT '操作原因',
    approval_status VARCHAR(20) DEFAULT 'approved' COMMENT '审批状态:pending/approved/rejected',
    approved_by BIGINT COMMENT '审批人',
    approved_at TIMESTAMP COMMENT '审批时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_action (action),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- ============================================================
-- 简历导入表 (resume_imports)
-- ============================================================
CREATE TABLE resume_imports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    source_type VARCHAR(30) NOT NULL COMMENT '来源类型:paste/github/manual',
    raw_content TEXT NOT NULL COMMENT '原始内容',
    parsed_content JSON COMMENT '解析后的结构化内容',
    import_status VARCHAR(20) DEFAULT 'pending' COMMENT '导入状态:pending/processing/completed/failed',
    error_message TEXT COMMENT '错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历导入表';

-- ============================================================
-- 用户任务执行记录表 (task_execution_logs) - 附录E新增
-- ============================================================
CREATE TABLE task_execution_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    action VARCHAR(30) COMMENT 'start/complete/pause/resume/cancel',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task_user (task_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户任务执行记录表';

-- ============================================================
-- 用户成长快照表 (user_growth_snapshots) - 附录E新增
-- ============================================================
CREATE TABLE user_growth_snapshots (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    snapshot_date DATE,
    skill_summary JSON COMMENT '技能摘要',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_date (user_id, snapshot_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户成长快照表';

-- ============================================================
-- AI上下文缓存表 (ai_contexts) - 附录E新增
-- ============================================================
CREATE TABLE ai_contexts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_id VARCHAR(100),
    context_summary TEXT COMMENT '上下文摘要',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_session (user_id, session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI上下文缓存表';
