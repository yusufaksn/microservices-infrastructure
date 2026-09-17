-- KEYS[1] -> ticket-pages:{page}:{size}
-- ARGV[1] -> ticket key prefix (e.g., "tickets:")

local pageKey = KEYS[1]
local ticketPrefix = ARGV[1]

-- 1. Read the list of ticket IDs for the page
local ids = redis.call('LRANGE', pageKey, 0, -1)

if not ids or #ids == 0 then
    return nil -- Cache Miss
end

-- 2. Construct the array of "tickets:{id}" keys
local ticketKeys = {}
for i, id in ipairs(ids) do
    table.insert(ticketKeys, ticketPrefix .. id)
end

-- 3. Fetch ticket details in bulk using MGET
local tickets = redis.call('MGET', unpack(ticketKeys))

-- Return the script result (Array containing JSON strings for each ticket)
return tickets