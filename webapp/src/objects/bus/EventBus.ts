import mitt from 'mitt';

type Events = {
  refreshFriendInvites: void;
};

const eventBus = mitt<Events>();
export default eventBus;