//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: ui_preferences.proto

@kotlin.jvm.JvmName("-initializehomePreferences")
inline fun homePreferences(block: HomePreferencesKt.Dsl.() -> kotlin.Unit): UiPreferences.HomePreferences =
  HomePreferencesKt.Dsl._create(UiPreferences.HomePreferences.newBuilder()).apply { block() }._build()
object HomePreferencesKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  class Dsl private constructor(
    private val _builder: UiPreferences.HomePreferences.Builder
  ) {
    companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: UiPreferences.HomePreferences.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): UiPreferences.HomePreferences = _builder.build()

    /**
     * <code>.Sorting sorting = 1;</code>
     */
     var sorting: UiPreferences.Sorting
      @JvmName("getSorting")
      get() = _builder.getSorting()
      @JvmName("setSorting")
      set(value) {
        _builder.setSorting(value)
      }
    /**
     * <code>.Sorting sorting = 1;</code>
     */
    fun clearSorting() {
      _builder.clearSorting()
    }
  }
}
@kotlin.jvm.JvmSynthetic
inline fun UiPreferences.HomePreferences.copy(block: HomePreferencesKt.Dsl.() -> kotlin.Unit): UiPreferences.HomePreferences =
  HomePreferencesKt.Dsl._create(this.toBuilder()).apply { block() }._build()
