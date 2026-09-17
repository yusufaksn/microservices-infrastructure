local pageKey = KEYS[1]
local ttlSeconds = tonumber(ARGV[1])
local idsCsv = ARGV[2]

-- 1. CHECK: Is ARGV[1] a valid positive integer for TTL?
if not ttlSeconds or ttlSeconds <= 0 then
    return redis.error_reply("ERR_INVALID_TTL: ARGV[1] must be a positive integer")
end

-- 2. CHECK: Are the individual ticket arguments properly paired (Key-Value)?
local totalPayloadArgs = #ARGV - 2
if totalPayloadArgs > 0 and (totalPayloadArgs % 2 ~= 0) then
    return redis.error_reply("ERR_INVALID_PAYLOAD: Key-Value arguments must be paired")
end

-- 3. CHECK: Do all key arguments start with the expected 'tickets:' prefix?
if totalPayloadArgs > 0 then
    for i = 3, #ARGV, 2 do
        if not string.match(ARGV[i], "^tickets:") then
            return redis.error_reply("ERR_INVALID_KEY_PREFIX: ARGV[" .. i .. "] must start with 'tickets:'")
        end
    end
end

-- =========================================================
-- ALL CHECKS PASSED, EXECUTE MAIN LOGIC
-- =========================================================

-- Clear existing page key and store new ID list
redis.call('DEL', pageKey)

if idsCsv and #idsCsv > 0 then
    for id in string.gmatch(idsCsv, "([^,]+)") do
        redis.call('RPUSH', pageKey, id)
    end
    redis.call('EXPIRE', pageKey, ttlSeconds)
end

-- Batch set individual ticket entities
if totalPayloadArgs > 0 then
    local msetArgs = {}
    for i = 3, #ARGV do
        table.insert(msetArgs, ARGV[i])
    end
    
    redis.call('MSET', unpack(msetArgs))
    
    for i = 3, #ARGV, 2 do
        redis.call('EXPIRE', ARGV[i], ttlSeconds)
    end
end

return "OK"