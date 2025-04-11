-- KEYS[1]: Hash key (survey:{surveyId}:question:{questionId}:results)
-- KEYS[2]: Channel (survey:{surveyId}:question:{questionId}:results)
-- KEYS[3]: Hosts set (survey:hosts:{surveyId})

-- ARGV[1]: Choice ID
-- ARGV[4]: Question ID

local hashKey = KEYS[1]
local channel = KEYS[2]
local hostsKey = KEYS[3]
local choiceId = ARGV[1]
local questionId = ARGV[2]

-- Increment vote count
local newCount = redis.call('HINCRBY', hashKey, choiceId, 1)

-- Check active hosts
local hostCount = redis.call('SCARD', hostsKey)

-- Publish if hosts are active
if hostCount > 0 then
    local payload = '{"questionId":"' .. questionId ..
                    '","choiceId":"' .. choiceId .. '","total":' .. newCount .. '}'
    redis.call('PUBLISH', channel, payload)
end

return newCount