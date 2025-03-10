import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { ScheduleModule } from '@nestjs/schedule';
import { ScheduledModule } from './services/scheduled/scheduled.module';

@Module({
  imports: [ScheduleModule.forRoot(), ScheduledModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
