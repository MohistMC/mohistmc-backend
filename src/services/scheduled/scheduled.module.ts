import { Module } from '@nestjs/common';
import { GithubBuildsScheduled } from './github-builds.scheduled';

@Module({
  imports: [],
  controllers: [],
  providers: [GithubBuildsScheduled],
})
export class ScheduledModule {}
