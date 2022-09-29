package dev.olshevski.navigation.reimagined

private const val PACKAGE_KEY = "dev.olshevski.navigation.reimagined.key"

internal fun viewModelStoreProviderKey(hostId: NavHostId) = "$PACKAGE_KEY:$hostId"

internal fun savedStateKey(hostId: NavHostId, entryId: NavId) = "$PACKAGE_KEY:$hostId:$entryId"