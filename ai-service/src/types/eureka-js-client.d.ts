declare module 'eureka-js-client' {
  // Minimal typing to satisfy TS compilation in this repo.
  // We only use: new Eureka(config) and eureka.start(callback)
  export class Eureka {
    constructor(config: any);
    start(callback: (error?: any) => void): void;
    stop(callback?: () => void): void;
    getInstancesByAppId(appId: string): any[];
  }
}

