package ug.global.temp.printer

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrinterModule {

    @Provides
    @Singleton
    fun providePrinterService(impl: GenericBluetoothPrinterService): PrinterService {
        return impl
    }
}
