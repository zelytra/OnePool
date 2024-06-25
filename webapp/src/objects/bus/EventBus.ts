import mitt from 'mitt';

export type MittEvents = {
  refreshFriendInvites: void;
  joinPoolParty: string;
};

const eventBus = mitt<MittEvents>();
export default eventBus;