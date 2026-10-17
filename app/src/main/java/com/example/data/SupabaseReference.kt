package com.example.data

object SupabaseReference {

    val SQL_SCHEMA_AND_RLS = """
-- ==========================================================
-- CoupleGuard: PostgreSQL Schema & Row Level Security (Supabase)
-- ==========================================================

-- 1. Profiles Table (Auth Users)
create table if not exists public.profiles (
  id uuid references auth.users on delete cascade primary key,
  full_name text not null,
  phone text,
  battery_percent int check (battery_percent between 0 and 100),
  is_charging boolean default false,
  is_online boolean default true,
  last_active_at timestamptz default now(),
  created_at timestamptz default now()
);

alter table public.profiles enable row level security;

-- 2. Couples Pairing Table
create table if not exists public.couples (
  id uuid primary key default gen_random_uuid(),
  user_a uuid references public.profiles(id) on delete cascade not null,
  user_b uuid references public.profiles(id) on delete set null,
  invite_code text unique not null,
  status text check (status in ('pending', 'active', 'disconnected')) default 'pending',
  paired_at timestamptz,
  created_at timestamptz default now(),
  constraint different_users check (user_a <> user_b)
);

alter table public.couples enable row level security;

-- 3. Mutual Location Sharing Table
create table if not exists public.locations (
  user_id uuid references public.profiles(id) on delete cascade primary key,
  couple_id uuid references public.couples(id) on delete cascade not null,
  latitude double precision not null,
  longitude double precision not null,
  address text,
  accuracy_meters real default 10.0,
  motion_activity text default 'stationary',
  is_sharing_enabled boolean default false,
  expires_at timestamptz,
  updated_at timestamptz default now()
);

alter table public.locations enable row level security;

-- 4. Emergency SOS Alerts Table
create table if not exists public.emergency_alerts (
  id uuid primary key default gen_random_uuid(),
  couple_id uuid references public.couples(id) on delete cascade not null,
  sender_id uuid references public.profiles(id) on delete cascade not null,
  status text check (status in ('active', 'resolved')) default 'active',
  latitude double precision not null,
  longitude double precision not null,
  address text,
  battery_percent int,
  custom_message text,
  triggered_at timestamptz default now(),
  resolved_at timestamptz,
  resolution_note text
);

alter table public.emergency_alerts enable row level security;

-- 5. Safety Check-Ins Table
create table if not exists public.safety_check_ins (
  id uuid primary key default gen_random_uuid(),
  couple_id uuid references public.couples(id) on delete cascade not null,
  creator_id uuid references public.profiles(id) on delete cascade not null,
  title text not null,
  scheduled_time timestamptz not null,
  duration_minutes int not null,
  note text,
  status text check (status in ('pending', 'confirmed_safe', 'missed')) default 'pending',
  confirmed_at timestamptz,
  created_at timestamptz default now()
);

alter table public.safety_check_ins enable row level security;

-- 6. Shared Timeline Events Table
create table if not exists public.timeline_events (
  id uuid primary key default gen_random_uuid(),
  couple_id uuid references public.couples(id) on delete cascade not null,
  actor_id uuid references public.profiles(id) on delete cascade not null,
  event_type text not null,
  title text not null,
  description text not null,
  severity text check (severity in ('info', 'success', 'warning', 'critical')) default 'info',
  created_at timestamptz default now()
);

alter table public.timeline_events enable row level security;

-- ==========================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- ==========================================================

-- Helper function: Is user part of the couple?
create or replace function public.is_couple_member(target_couple_id uuid)
returns boolean language sql security definer as $$
  select exists (
    select 1 from public.couples
    where id = target_couple_id
    and status = 'active'
    and (user_a = auth.uid() or user_b = auth.uid())
  );
$$;

-- Locations RLS: Only authorized active partner can view location
create policy "Users can view partner location only if consented"
on public.locations for select
using (
  auth.uid() = user_id or (
    public.is_couple_member(couple_id)
    and is_sharing_enabled = true
    and (expires_at is null or expires_at > now())
  )
);

create policy "Users can update only their own location"
on public.locations for insert with check (auth.uid() = user_id);

create policy "Users can modify only their own location sharing status"
on public.locations for update using (auth.uid() = user_id);

-- Emergency Alerts RLS: Visible to both partners in the couple
create policy "Couple members can view emergency alerts"
on public.emergency_alerts for select
using (public.is_couple_member(couple_id));

create policy "Users can trigger emergency alerts"
on public.emergency_alerts for insert
with check (auth.uid() = sender_id and public.is_couple_member(couple_id));

-- Check-ins RLS: Visible to both partners
create policy "Couple members can view check-ins"
on public.safety_check_ins for select
using (public.is_couple_member(couple_id));

create policy "Creators can manage check-ins"
on public.safety_check_ins for all
using (auth.uid() = creator_id);
    """.trimIndent()

    val WINDOWS_SETUP_GUIDE = """
# CoupleGuard: Windows & Cross-Platform Setup Instructions

### 1. Prerequisites (Windows 10/11)
- Install Node.js LTS (v20+): https://nodejs.org
- Install Git: https://git-scm.com
- (Optional for React Native / Expo):
  npm install -g eas-cli expo-cli

### 2. Environment Variables Configuration (.env)
Never commit secrets or service role keys to Git.
Create a `.env` file in the root:
EXPO_PUBLIC_SUPABASE_URL=https://your-project.supabase.co
EXPO_PUBLIC_SUPABASE_ANON_KEY=eyJhbGciOi...

### 3. Installation & Run Commands
# Clone the repository
git clone https://github.com/your-org/couple-guard.git
cd couple-guard

# Install dependencies
npm install

# Run Expo development server
npx expo start

# For Android Studio (Native Kotlin Applet):
# Open in Android Studio or compile with Gradle:
gradle assembleDebug

### 4. Supabase Setup in 3 Steps
1. Create a new project in Supabase dashboard.
2. Go to SQL Editor, paste the SQL schema & RLS policies provided in CoupleGuard.
3. Enable Email/Phone auth or Google Sign-In in Authentication -> Providers.
    """.trimIndent()
}
