-- =============================================================================
-- PREMIUM SUBSCRIPTION FEATURE SCHEMA (PostgreSQL)
-- Domain-Driven Isolation for CodeCanvas User Service
-- =============================================================================

-- Table: subscription_plans
CREATE TABLE IF NOT EXISTS subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    tier VARCHAR(50) NOT NULL UNIQUE,
    price DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    billing_cycle_days INTEGER NOT NULL,
    max_snippets_per_month INTEGER,
    ai_requests_per_month INTEGER,
    priority_support BOOLEAN DEFAULT FALSE,
    custom_badge VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    
    CONSTRAINT chk_price_positive CHECK (price >= 0),
    CONSTRAINT chk_billing_cycle_positive CHECK (billing_cycle_days > 0)
);

CREATE INDEX IF NOT EXISTS idx_subscription_plans_tier ON subscription_plans(tier);
CREATE INDEX IF NOT EXISTS idx_subscription_plans_active ON subscription_plans(is_active);

-- Table: user_subscriptions
CREATE TABLE IF NOT EXISTS user_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    plan_id BIGINT NOT NULL,
    subscription_status VARCHAR(50) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP,
    renewal_date TIMESTAMP,
    payment_method VARCHAR(100),
    payment_id VARCHAR(255) UNIQUE,
    is_auto_renew BOOLEAN DEFAULT TRUE,
    cancellation_reason TEXT,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id) ON DELETE RESTRICT,
    CONSTRAINT chk_status CHECK (subscription_status IN (
        'ACTIVE', 'CANCELLED', 'EXPIRED', 'PENDING', 'SUSPENDED'
    )),
    CONSTRAINT chk_end_after_start CHECK (ends_at IS NULL OR ends_at > started_at)
);

CREATE INDEX IF NOT EXISTS idx_user_subscriptions_user_id ON user_subscriptions(user_id);
CREATE INDEX IF NOT EXISTS idx_user_subscriptions_status ON user_subscriptions(subscription_status);
CREATE INDEX IF NOT EXISTS idx_user_subscriptions_renewal ON user_subscriptions(renewal_date);
CREATE INDEX IF NOT EXISTS idx_user_subscriptions_user_status ON user_subscriptions(user_id, subscription_status);

-- Table: subscription_history
CREATE TABLE IF NOT EXISTS subscription_history (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    subscription_id BIGINT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    previous_plan_id BIGINT,
    new_plan_id BIGINT,
    description TEXT,
    transaction_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_id) REFERENCES user_subscriptions(id) ON DELETE CASCADE,
    CONSTRAINT chk_event_type CHECK (event_type IN (
        'SUBSCRIPTION_CREATED', 'SUBSCRIPTION_RENEWED', 'PLAN_UPGRADED',
        'PLAN_DOWNGRADED', 'SUBSCRIPTION_CANCELLED', 'SUBSCRIPTION_SUSPENDED',
        'PAYMENT_FAILED', 'AUTO_RENEWAL_DISABLED'
    ))
);

CREATE INDEX IF NOT EXISTS idx_subscription_history_user_id ON subscription_history(user_id);
CREATE INDEX IF NOT EXISTS idx_subscription_history_type ON subscription_history(event_type);
CREATE INDEX IF NOT EXISTS idx_subscription_history_created ON subscription_history(created_at DESC);

-- Table: usage_tracking
CREATE TABLE IF NOT EXISTS usage_tracking (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    subscription_id BIGINT,
    metric_name VARCHAR(100) NOT NULL,
    current_count INTEGER DEFAULT 0,
    limit_count INTEGER,
    reset_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_id) REFERENCES user_subscriptions(id) ON DELETE SET NULL,
    CONSTRAINT chk_count_non_negative CHECK (current_count >= 0)
);

CREATE INDEX IF NOT EXISTS idx_usage_tracking_user_metric ON usage_tracking(user_id, metric_name);

-- Initial Subscription Plans Seed Data
INSERT INTO subscription_plans (name, description, tier, price, currency, billing_cycle_days, max_snippets_per_month, ai_requests_per_month, priority_support, custom_badge, is_active, created_by)
VALUES 
('Free Tier', 'Basic access to CodeCanvas with standard limits', 'FREE', 0.00, 'INR', 30, 20, 5, FALSE, 'Free User', TRUE, 'SYSTEM')
ON CONFLICT (tier) DO NOTHING;

INSERT INTO subscription_plans (name, description, tier, price, currency, billing_cycle_days, max_snippets_per_month, ai_requests_per_month, priority_support, custom_badge, is_active, created_by)
VALUES 
('Basic Premium', 'Enhanced snippet storage and increased AI assistance requests', 'BASIC_PREMIUM', 299.00, 'INR', 30, 100, 100, FALSE, 'Pro Member', TRUE, 'SYSTEM')
ON CONFLICT (tier) DO NOTHING;

INSERT INTO subscription_plans (name, description, tier, price, currency, billing_cycle_days, max_snippets_per_month, ai_requests_per_month, priority_support, custom_badge, is_active, created_by)
VALUES 
('Pro Premium', 'Unlimited power with maximum AI requests and priority support', 'PRO_PREMIUM', 799.00, 'INR', 30, 1000, 1000, TRUE, 'Pro Elite', TRUE, 'SYSTEM')
ON CONFLICT (tier) DO NOTHING;
