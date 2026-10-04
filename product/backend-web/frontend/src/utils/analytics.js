import { backendURL } from 'src/data/lookups.js'

const ANALYTICS_ENDPOINT = `${backendURL}analytics/events`

function getOrCreateSessionId() {
  const storageKey = 'crosswords_session_id'
  const existingSessionId = localStorage.getItem(storageKey)

  if (existingSessionId) {
    return existingSessionId
  }

  const newSessionId = `cw_${Date.now()}_${Math.random().toString(36).slice(2)}`
  localStorage.setItem(storageKey, newSessionId)

  return newSessionId
}

function getTrackedEvents() {
  try {
    return JSON.parse(sessionStorage.getItem('crosswords_tracked_events') || '[]')
  } catch {
    return []
  }
}

function wasEventTracked(eventName) {
  return getTrackedEvents().includes(eventName)
}

function markEventAsTracked(eventName) {
  const trackedEvents = getTrackedEvents()

  if (!trackedEvents.includes(eventName)) {
    trackedEvents.push(eventName)
  }

  sessionStorage.setItem('crosswords_tracked_events', JSON.stringify(trackedEvents))
}

export async function sendAnalyticsEvent(eventName, payload = {}, options = {}) {
  const { once = false } = options

  if (once && wasEventTracked(eventName)) {
    return
  }

  try {
    await fetch(ANALYTICS_ENDPOINT, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      keepalive: true,
      body: JSON.stringify({
        event: eventName,
        session_id: getOrCreateSessionId(),
        page: window.location.pathname,
        timestamp: new Date().toISOString(),
        ...payload
      })
    })

    if (once) {
      markEventAsTracked(eventName)
    }
  } catch (error) {
    console.error('Ошибка отправки аналитики:', error)
  }
}