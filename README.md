# capstone-project


```mermaid
erDiagram
    app_user {
        bigint id PK
        varchar email
        varchar password
        nvarchar full_name
        date date_of_birth
        varchar gender
        varchar phone
        nvarchar bio
        nvarchar avatar_url
        nvarchar cccd_url
        varchar role
        bit enabled
        varchar verification_token
        varchar facebook
        varchar instagram
        varchar tiktok
        float average_rating
        int review_count
        nvarchar interests
        nvarchar experience
        nvarchar certificate_number
        nvarchar certificate_url
        bit is_local_guide
        bit is_international_guide
        bit kyc_approved
        varchar kyc_rejection_reason
        varchar bank_name
        varchar account_number
        varchar account_holder
    }
    
    user_request {
        bigint id PK
        varchar email
        varchar password
        nvarchar full_name
        nvarchar cccd_url
        varchar verification_token
    }
    
    guide_request {
        bigint id PK
        bigint user_id FK
        nvarchar cccd_url
        nvarchar experience
        nvarchar certificate_number
        nvarchar certificate_url
        bit is_local_guide
        bit is_international_guide
        varchar status
        nvarchar reason
        datetime2 created_at
    }
    
    guide_service {
        bigint id PK
        bigint guide_id FK
        varchar city
        varchar type
        varchar language
        varchar group_size_category
        float price_per_day
        varchar payment_option
    }
    
    booking {
        bigint id PK
        bigint customer_id FK
        bigint guide_id FK
        bigint guide_service_id FK
        date start_date
        date end_date
        nvarchar destination
        float total_price
        varchar status
        datetime2 created_at
        datetime2 canceled_at
        nvarchar reason
    }
    
    day_off {
        bigint id PK
        bigint guide_id FK
        bigint booking_id
        date date
        bit auto_generated
    }
    
    payment {
        bigint id PK
        bigint booking_id FK
        float amount
        varchar type
        varchar status
        nvarchar sender_account_name
        nvarchar sender_account_number
        nvarchar transaction_note
        nvarchar transaction_image_url
        datetime2 created_at
    }
    
    review {
        bigint id PK
        bigint booking_id FK
        bigint reviewer_id FK
        bigint reviewed_user_id FK
        int rating
        nvarchar feedback
        datetime2 review_date
    }
    
    post {
        bigint id PK
        bigint user_id FK
        nvarchar title
        nvarchar content
        varchar category
        datetime2 created_at
    }
    
    comment {
        bigint id PK
        bigint post_id FK
        bigint user_id FK
        bigint parent_id FK
        nvarchar content
        datetime2 created_at
    }
    
    post_like {
        bigint id PK
        bigint post_id FK
        bigint user_id FK
        bit is_liked
        datetime2 created_at
    }
    
    chat_message {
        bigint id PK
        bigint sender_id FK
        bigint receiver_id FK
        nvarchar content
        datetime2 timestamp
        bit is_read
    }
    
    notification {
        bigint id PK
        bigint user_id FK
        nvarchar message
        nvarchar source_link
        bit is_read
        datetime2 created_at
    }
    
    report {
        bigint id PK
        bigint reporter_id FK
        varchar report_type
        bigint target_id
        nvarchar target_name
        nvarchar reason
        datetime2 report_time
        bit resolved
        datetime2 resolved_time
        nvarchar admin_feed_back
    }
    
    app_user ||--o{ guide_request : "requests to become"
    app_user ||--o{ guide_service : "offers"
    app_user ||--o{ booking : "as customer"
    app_user ||--o{ booking : "as guide"
    app_user ||--o{ day_off : "has"
    app_user ||--o{ post : "creates"
    app_user ||--o{ comment : "writes"
    app_user ||--o{ post_like : "makes"
    app_user ||--o{ chat_message : "sends"
    app_user ||--o{ chat_message : "receives"
    app_user ||--o{ notification : "receives"
    app_user ||--o{ report : "reports"
    app_user ||--o{ review : "reviews"
    app_user ||--o{ review : "is reviewed"
    
    guide_service ||--o{ booking : "is used in"
    
    booking ||--o{ payment : "has"
    booking ||--o{ day_off : "generates"
    booking ||--o{ review : "generates"
    
    post ||--o{ comment : "has"
    post ||--o{ post_like : "receives"
    
    comment ||--o{ comment : "has replies"```
