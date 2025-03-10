import { Injectable, Logger } from '@nestjs/common';
import { Interval } from '@nestjs/schedule';

@Injectable()
export class GithubBuildsScheduled {
  private readonly logger = new Logger(GithubBuildsScheduled.name);

  @Interval(10000)
  synchronizeBuilds() {
    this.logger.debug('Called when the current second is 45');
  }
}
